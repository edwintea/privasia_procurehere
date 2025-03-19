/*
 * jQuery Plugin: token1izing Autocomplete Text Entry
 * Version 1.6.0
 *
 * Copyright (c) 2009 James Smith (http://loopj.com)
 * Licensed jointly under the GPL and MIT licenses,
 * choose which one suits your project best!
 *
 */

(function ($) {
// Default settings
var DEFAULT_SETTINGS = {
	// Search settings
    method1: "GET",
    contentType1: "json",
    queryParam1: "q",
    searchDelay: 300,
    minChars: 1,
    propertyToSearch: "name",
    propertyToSearchCode : "code",
    jsonContainer: null,

	// Display settings
    hintText: "Type in a search term",
    noResultsText: "No results",
    searchingText: "Searching...",
    deleteText: "&times;",
    animateDropdown: true,

	// token1ization settings
    token1Limit: null,
    token1Delimiter: ",",
    preventDuplicates: false,

	// Output settings
    token1Value: "id",

	// Prepopulation settings
    prePopulate: null,
    processPrePopulate: false,

	// Manipulation settings
    idPrefix: "token1-input-",

	// Formatters
    resultsFormatter: function(item){ return "<li>"+ item[this.propertyToSearchCode]+ '-' + item[this.propertyToSearch]+ "</li>" },
    token1Formatter: function(item) { return "<li><input type='hidden' name='catFav' value='"+item[this.token1Value]+"'/><p>"+ item[this.propertyToSearchCode]+ '-' + item[this.propertyToSearch] + "</p></li>" },

	// Callbacks
    onResult: null,
    onAdd: null,
    onDelete: null,
    onReady: null
};

// Default classes to use when theming
var DEFAULT_CLASSES = {
    token1List: "token-input-list",
    token1: "token-input-token",
    token1Delete: "token-input-delete-token",
    selectedtoken1: "token-input-selected-token",
    highlightedtoken1: "token-input-highlighted-token",
    dropdown: "token-input-dropdown",
    dropdownItem: "token-input-dropdown-item",
    dropdownItem2: "token-input-dropdown-item2",
    selectedDropdownItem: "token-input-selected-dropdown-item",
    inputtoken1: "token-input-input-token"
};

// Input box position "enum"
var POSITION = {
    BEFORE: 0,
    AFTER: 1,
    END: 2
};

// Keys "enum"
var KEY = {
    BACKSPACE: 8,
    TAB: 9,
    ENTER: 13,
    ESCAPE: 27,
    SPACE: 32,
    PAGE_UP: 33,
    PAGE_DOWN: 34,
    END: 35,
    HOME: 36,
    LEFT: 37,
    UP: 38,
    RIGHT: 39,
    DOWN: 40,
    NUMPAD_ENTER: 108,
    COMMA: 188
};

// Additional public (exposed) methods
var methods = {
    init: function(url_or_data_or_function, options) {
        var settings = $.extend({}, DEFAULT_SETTINGS, options || {});

        return this.each(function () {
            $(this).data("tokenInputObject", new $.token1List(this, url_or_data_or_function, settings));
        });
    },
    clear: function() {
        this.data("tokenInputObject").clear();
        return this;
    },
    add: function(item) {
        this.data("tokenInputObject").add(item);
        return this;
    },
    remove: function(item) {
        this.data("tokenInputObject").remove(item);
        return this;
    },
    get: function() {
    	return this.data("tokenInputObject").gettoken1s();
   	}
}

// Expose the .tokenInput function to jQuery as a plugin
$.fn.tokenInput1 = function (method1) {
    // Method calling and initialization logic
    if(methods[method1]) {
        return methods[method1].apply(this, Array.prototype.slice.call(arguments, 1));
    } else {
        return methods.init.apply(this, arguments);
    }
};

// token1List class for each input
$.token1List = function (input, url_or_data, settings) {
    //
    // Initialization
    //

    // Configure the data source
    if($.type(url_or_data) === "string" || $.type(url_or_data) === "function") {
        // Set the url to query against
        settings.url = url_or_data;

        // If the URL is a function, evaluate it here to do our initalization work
        var url = computeURL();

        // Make a smart guess about cross-domain if it wasn't explicitly specified
        if(settings.crossDomain === undefined) {
            if(url.indexOf("://") === -1) {
                settings.crossDomain = false;
            } else {
                settings.crossDomain = (location.href.split(/\/+/g)[1] !== url.split(/\/+/g)[1]);
            }
        }
    } else if(typeof(url_or_data) === "object") {
        // Set the local data to search through
        settings.local_data = url_or_data;
    }

    // Build class names
    if(settings.classes) {
        // Use custom class names
        settings.classes = $.extend({}, DEFAULT_CLASSES, settings.classes);
    } else if(settings.theme) {
        // Use theme-suffixed default class names
        settings.classes = {};
        $.each(DEFAULT_CLASSES, function(key, value) {
            settings.classes[key] = value + "-" + settings.theme;
        });
    } else {
        settings.classes = DEFAULT_CLASSES;
    }


    // Save the token1s
    var saved_token1s = [];

    // Keep track of the number of token1s in the list
    var token1_count = 0;

    // Basic cache to save on db hits
    var cache = new $.token1List.Cache();

    // Keep track of the timeout, old vals
    var timeout;
    var input_val;

    // Create a new text input an attach keyup events
    var input_box = $("<input type=\"text\" class=\"form-control\"  autocomplete=\"off\">")
        .css({
            outline: "none"
        })
        .attr("id", settings.idPrefix + input.id)
        .focus(function () {
            if (settings.token1Limit === null || settings.token1Limit !== token1_count) {
                show_dropdown_hint();
            }
        })
        .blur(function () {
            hide_dropdown();
            $(this).val("");
        })
        .bind("keyup keydown blur update", resize_input)
        .keydown(function (event) {
            var previous_token1;
            var next_token1;

            switch(event.keyCode) {
                case KEY.LEFT:
                case KEY.RIGHT:
                case KEY.UP:
                case KEY.DOWN:
                    if(!$(this).val()) {
                        previous_token1 = input_token1.prev();
                        next_token1 = input_token1.next();

                        if((previous_token1.length && previous_token1.get(0) === selected_token1) || (next_token1.length && next_token1.get(0) === selected_token1)) {
                            // Check if there is a previous/next token1 and it is selected
                            if(event.keyCode === KEY.LEFT || event.keyCode === KEY.UP) {
                                deselect_token1($(selected_token1), POSITION.BEFORE);
                            } else {
                                deselect_token1($(selected_token1), POSITION.AFTER);
                            }
                        } else if((event.keyCode === KEY.LEFT || event.keyCode === KEY.UP) && previous_token1.length) {
                            // We are moving left, select the previous token1 if it exists
                            select_token1($(previous_token1.get(0)));
                        } else if((event.keyCode === KEY.RIGHT || event.keyCode === KEY.DOWN) && next_token1.length) {
                            // We are moving right, select the next token1 if it exists
                            select_token1($(next_token1.get(0)));
                        }
                    } else {
                        var dropdown_item = null;

                        if(event.keyCode === KEY.DOWN || event.keyCode === KEY.RIGHT) {
                            dropdown_item = $(selected_dropdown_item).next();
                        } else {
                            dropdown_item = $(selected_dropdown_item).prev();
                        }

                        if(dropdown_item.length) {
                            select_dropdown_item(dropdown_item);
                        }
                        return false;
                    }
                    break;

                case KEY.BACKSPACE:
                    previous_token1 = input_token1.prev();

                    if(!$(this).val().length) {
                        if(selected_token1) {
                            delete_token1($(selected_token1));
                            hidden_input.change();
                        } else if(previous_token1.length) {
                            select_token1($(previous_token1.get(0)));
                        }

                        return false;
                    } else if($(this).val().length === 1) {
                        hide_dropdown();
                    } else {
                        // set a timeout just long enough to let this function finish.
                        setTimeout(function(){do_search();}, 5);
                    }
                    break;

                case KEY.TAB:
                case KEY.ENTER:
                case KEY.NUMPAD_ENTER:
                case KEY.COMMA:
                  if(selected_dropdown_item) {
                    add_token1($(selected_dropdown_item).data("tokeninput"));
                    hidden_input.change();
                    return false;
                  }
                  break;

                case KEY.ESCAPE:
                  hide_dropdown();
                  return true;

                default:
                    if(String.fromCharCode(event.which)) {
                        // set a timeout just long enough to let this function finish.
                        setTimeout(function(){do_search();}, 5);
                    }
                    break;
            }
        });

    // Keep a reference to the original input box
    var hidden_input = $(input)
                           .hide()
                           .val("")
                           .focus(function () {
                               input_box.focus();
                           })
                           .blur(function () {
                               input_box.blur();
                           });

    // Keep a reference to the selected token1 and dropdown item
    var selected_token1 = null;
    var selected_token1_index = 0;
    var selected_dropdown_item = null;

    // The list to store the token1 items in
    var token1_list = $("<ul />")
        .addClass(settings.classes.token1List)
        .click(function (event) {
            var li = $(event.target).closest("li");
            if(li && li.get(0) && $.data(li.get(0), "tokeninput")) {
                toggle_select_token1(li);
            } else {
                // Deselect selected token1
                if(selected_token1) {
                    deselect_token1($(selected_token1), POSITION.END);
                }

                // Focus input box
                input_box.focus();
            }
        })
        .mouseover(function (event) {
            var li = $(event.target).closest("li");
            if(li && selected_token1 !== this) {
                li.addClass(settings.classes.highlightedtoken1);
            }
        })
        .mouseout(function (event) {
            var li = $(event.target).closest("li");
            if(li && selected_token1 !== this) {
                li.removeClass(settings.classes.highlightedtoken1);
            }
        })
        .insertBefore(hidden_input);

    // The token1 holding the input box
    var input_token1 = $("<li />")
        .addClass(settings.classes.inputtoken1)
        .appendTo(token1_list)
        .append(input_box);

    // The list to store the dropdown items in
    var dropdown = $("<div>")
        .addClass(settings.classes.dropdown)
        .appendTo(".selectListAjax1")
        .hide();

    // Magic element to help us resize the text input
    var input_resizer = $("<tester/>")
        .insertAfter(input_box)
        .css({
            position: "absolute",
            top: -9999,
            left: -9999,
            width: "auto",
            fontSize: input_box.css("fontSize"),
            fontFamily: input_box.css("fontFamily"),
            fontWeight: input_box.css("fontWeight"),
            letterSpacing: input_box.css("letterSpacing"),
            whiteSpace: "nowrap"
        });

    // Pre-populate list if items exist
    hidden_input.val("");
    var li_data = settings.prePopulate || hidden_input.data("pre");
    if(settings.processPrePopulate && $.isFunction(settings.onResult)) {
        li_data = settings.onResult.call(hidden_input, li_data);
    }
    if(li_data && li_data.length) {
        $.each(li_data, function (index, value) {
            insert_token1(value);
            checktoken1Limit();
        });
    }

    // Initialization is done
    if($.isFunction(settings.onReady)) {
        settings.onReady.call();
    }

    //
    // Public functions
    //

    this.clear = function() {
        token1_list.children("li").each(function() {
            if ($(this).children("input").length === 0) {
                delete_token1($(this));
            }
        });
    }

    this.add = function(item) {
        add_token1(item);
    }

    this.remove = function(item) {
        token1_list.children("li").each(function() {
            if ($(this).children("input").length === 0) {
                var currtoken1 = $(this).data("tokeninput");
                var match = true;
                for (var prop in item) {
                    if (item[prop] !== currtoken1[prop]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    delete_token1($(this));
                }
            }
        });
    }
    
    this.gettoken1s = function() {
   		return saved_token1s;
   	}

    //
    // Private functions
    //

    function checktoken1Limit() {
        if(settings.token1Limit !== null && token1_count >= settings.token1Limit) {
            input_box.hide();
            hide_dropdown();
            return;
        }
    }

    function resize_input() {
        if(input_val === (input_val = input_box.val())) {return;}

        // Enter new content into resizer and resize input accordingly
        var escaped = input_val.replace(/&/g, '&amp;').replace(/\s/g,' ').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        input_resizer.html(escaped);
        input_box.width(input_resizer.width() + 30);
    }

    function is_printable_character(keycode) {
        return ((keycode >= 48 && keycode <= 90) ||     // 0-1a-z
                (keycode >= 96 && keycode <= 111) ||    // numpad 0-9 + - / * .
                (keycode >= 186 && keycode <= 192) ||   // ; = , - . / ^
                (keycode >= 219 && keycode <= 222));    // ( \ ) '
    }

    // Inner function to a token1 to the list
    function insert_token1(item) {
        var this_token1 = settings.token1Formatter(item);
        this_token1 = $(this_token1)
          .addClass(settings.classes.token1)
          .insertBefore(input_token1);

        // The 'delete token1' button
        $("<span>" + settings.deleteText + "</span>")
            .addClass(settings.classes.token1Delete)
            .appendTo(this_token1)
            .click(function () {
                delete_token1($(this).parent());
                hidden_input.change();
                return false;
            });

        // Store data on the token1
        var token1_data = {"id": item.id};
        token1_data[settings.propertyToSearch] = item[settings.propertyToSearch];
        $.data(this_token1.get(0), "tokeninput", item);

        // Save this token1 for duplicate checking
        saved_token1s = saved_token1s.slice(0,selected_token1_index).concat([token1_data]).concat(saved_token1s.slice(selected_token1_index));
        selected_token1_index++;

        // Update the hidden input
        update_hidden_input(saved_token1s, hidden_input);

        token1_count += 1;

        // Check the token1 limit
        if(settings.token1Limit !== null && token1_count >= settings.token1Limit) {
            input_box.hide();
            hide_dropdown();
        }

        return this_token1;
    }

    // Add a token1 to the token1 list based on user input
    function add_token1 (item) {
        var callback = settings.onAdd;

        // See if the token1 already exists and select it if we don't want duplicates
        if(token1_count > 0 && settings.preventDuplicates) {
            var found_existing_token1 = null;
            token1_list.children().each(function () {
                var existing_token1 = $(this);
                var existing_data = $.data(existing_token1.get(0), "tokeninput");
                if(existing_data && existing_data.id === item.id) {
                    found_existing_token1 = existing_token1;
                    return false;
                }
            });

            if(found_existing_token1) {
                select_token1(found_existing_token1);
                input_token1.insertAfter(found_existing_token1);
                input_box.focus();
                return;
            }
        }

        // Insert the new token1s
        if(settings.token1Limit == null || token1_count < settings.token1Limit) {
            insert_token1(item);
            checktoken1Limit();
        }

        // Clear input box
        input_box.val("");

        // Don't show the help dropdown, they've got the idea
        hide_dropdown();

        // Execute the onAdd callback if defined
        if($.isFunction(callback)) {
            callback.call(hidden_input,item);
        }
    }

    // Select a token1 in the token1 list
    function select_token1 (token1) {
        token1.addClass(settings.classes.selectedtoken1);
        selected_token1 = token1.get(0);

        // Hide input box
        input_box.val("");

        // Hide dropdown if it is visible (eg if we clicked to select token1)
        hide_dropdown();
    }

    // Deselect a token1 in the token1 list
    function deselect_token1 (token1, position) {
        token1.removeClass(settings.classes.selectedtoken1);
        selected_token1 = null;

        if(position === POSITION.BEFORE) {
            input_token1.insertBefore(token1);
            selected_token1_index--;
        } else if(position === POSITION.AFTER) {
            input_token1.insertAfter(token1);
            selected_token1_index++;
        } else {
            input_token1.appendTo(token1_list);
            selected_token1_index = token1_count;
        }

        // Show the input box and give it focus again
        input_box.focus();
    }

    // Toggle selection of a token1 in the token1 list
    function toggle_select_token1(token1) {
        var previous_selected_token1 = selected_token1;

        if(selected_token1) {
            deselect_token1($(selected_token1), POSITION.END);
        }

        if(previous_selected_token1 === token1.get(0)) {
            deselect_token1(token1, POSITION.END);
        } else {
            select_token1(token1);
        }
    }

    // Delete a token1 from the token1 list
    function delete_token1 (token1) {
        // Remove the id from the saved list
        var token1_data = $.data(token1.get(0), "tokeninput");
        var callback = settings.onDelete;

        var index = token1.prevAll().length;
        if(index > selected_token1_index) index--;

        // Delete the token1
        token1.remove();
        selected_token1 = null;

        // Show the input box and give it focus again
        input_box.focus();

        // Remove this token1 from the saved list
        saved_token1s = saved_token1s.slice(0,index).concat(saved_token1s.slice(index+1));
        if(index < selected_token1_index) selected_token1_index--;

        // Update the hidden input
        update_hidden_input(saved_token1s, hidden_input);

        token1_count -= 1;

        if(settings.token1Limit !== null) {
            input_box
                .show()
                .val("")
                .focus();
        }

        // Execute the onDelete callback if defined
        if($.isFunction(callback)) {
            callback.call(hidden_input,token1_data);
        }
    }

    // Update the hidden input box value
    function update_hidden_input(saved_token1s, hidden_input) {
        var token1_values = $.map(saved_token1s, function (el) {
            return el[settings.token1Value];
        });
        hidden_input.val(token1_values.join(settings.token1Delimiter));

    }

    // Hide and clear the results dropdown
    function hide_dropdown () {
        dropdown.hide().empty();
        selected_dropdown_item = null;
    }

    function show_dropdown() {
        dropdown
            .css({
                position: "absolute",
                top: $(token1_list).offset().top + $(token1_list).outerHeight(),
                left: $(token1_list).offset().left,
                zindex: 999
            })
            .show();
    }

    function show_dropdown_searching () {
        if(settings.searchingText) {
            dropdown.html("<p>"+settings.searchingText+"</p>");
            show_dropdown();
        }
    }

    function show_dropdown_hint () {
        if(settings.hintText) {
            dropdown.html("<p>"+settings.hintText+"</p>");
            show_dropdown();
        }
    }

    // Highlight the query part of the search term
    function highlight_term(value, term) {
        return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + term + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<b>$1</b>");
    }
    
    function find_value_and_highlight_term(template, value, term) {
        return template.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + value + ")(?![^<>]*>)(?![^&;]+;)", "g"), highlight_term(value, term));
    }

    // Populate the results dropdown with some results
    function populate_dropdown (query, results) {
        if(results && results.length) {
            dropdown.empty();
            var dropdown_ul = $("<ul>")
                .appendTo(dropdown)
                .mouseover(function (event) {
                    select_dropdown_item($(event.target).closest("li"));
                })
                .mousedown(function (event) {
                    add_token1($(event.target).closest("li").data("tokeninput"));
                    hidden_input.change();
                    return false;
                })
                .hide();

            $.each(results, function(index, value) {
                var this_li = settings.resultsFormatter(value);
                
                this_li = find_value_and_highlight_term(this_li ,value[settings.propertyToSearch], query);            
                
                this_li = $(this_li).appendTo(dropdown_ul);
                
                if(index % 2) {
                    this_li.addClass(settings.classes.dropdownItem);
                } else {
                    this_li.addClass(settings.classes.dropdownItem2);
                }

                if(index === 0) {
                    select_dropdown_item(this_li);
                }

                $.data(this_li.get(0), "tokeninput", value);
            });

            show_dropdown();

            if(settings.animateDropdown) {
                dropdown_ul.slideDown("fast");
            } else {
                dropdown_ul.show();
            }
        } else {
            if(settings.noResultsText) {
                dropdown.html("<p>"+settings.noResultsText+"</p>");
                show_dropdown();
            }
        }
    }

    // Highlight an item in the results dropdown
    function select_dropdown_item (item) {
        if(item) {
            if(selected_dropdown_item) {
                deselect_dropdown_item($(selected_dropdown_item));
            }

            item.addClass(settings.classes.selectedDropdownItem);
            selected_dropdown_item = item.get(0);
        }
    }

    // Remove highlighting from an item in the results dropdown
    function deselect_dropdown_item (item) {
        item.removeClass(settings.classes.selectedDropdownItem);
        selected_dropdown_item = null;
    }

    // Do a search and show the "searching" dropdown if the input is longer
    // than settings.minChars
    function do_search() {
        var query = input_box.val().toLowerCase();

        if(query && query.length) {
            if(selected_token1) {
                deselect_token1($(selected_token1), POSITION.AFTER);
            }

            if(query.length >= settings.minChars) {
                show_dropdown_searching();
                clearTimeout(timeout);

                timeout = setTimeout(function(){
                    run_search(query);
                }, settings.searchDelay);
            } else {
                hide_dropdown();
            }
        }
    }

    // Do the actual search
    function run_search(query) {
        var cache_key = query + computeURL();
        var cached_results = cache.get(cache_key);
        if(cached_results) {
            populate_dropdown(query, cached_results);
        } else {
            // Are we doing an ajax search or local data search?
            if(settings.url) {
                var url = computeURL();
                // Extract exisiting get params
                var ajax_params = {};
                ajax_params.data = {};
                if(url.indexOf("?") > -1) {
                    var parts = url.split("?");
                    ajax_params.url = parts[0];

                    var param_array = parts[1].split("&");
                    $.each(param_array, function (index, value) {
                        var kv = value.split("=");
                        ajax_params.data[kv[0]] = kv[1];
                    });
                } else {
                    ajax_params.url = url;
                }

                // Prepare the request
                ajax_params.data[settings.queryParam1] = query;
                ajax_params.type = settings.method1;
                ajax_params.dataType = settings.contentType1;
                if(settings.crossDomain) {
                    ajax_params.dataType = "jsonp";
                }

                // Attach the success callback
                ajax_params.success = function(results) {
                  if($.isFunction(settings.onResult)) {
                      results = settings.onResult.call(hidden_input, results);
                  }
                  cache.add(cache_key, settings.jsonContainer ? results[settings.jsonContainer] : results);

                  // only populate the dropdown if the results are associated with the active search query
                  if(input_box.val().toLowerCase() === query) {
                      populate_dropdown(query, settings.jsonContainer ? results[settings.jsonContainer] : results);
                  }
                };

                // Make the request
                $.ajax(ajax_params);
            } else if(settings.local_data) {
                // Do the search through local data
                var results = $.grep(settings.local_data, function (row) {
                    return row[settings.propertyToSearch].toLowerCase().indexOf(query.toLowerCase()) > -1;
                });

                if($.isFunction(settings.onResult)) {
                    results = settings.onResult.call(hidden_input, results);
                }
                cache.add(cache_key, results);
                populate_dropdown(query, results);
            }
        }
    }

    // compute the dynamic URL
    function computeURL() {
        var url = settings.url;
        if(typeof settings.url == 'function') {
            url = settings.url.call();
        }
        return url;
    }
};

// Really basic cache for the results
$.token1List.Cache = function (options) {
    var settings = $.extend({
        max_size: 500
    }, options);

    var data = {};
    var size = 0;

    var flush = function () {
        data = {};
        size = 0;
    };

    this.add = function (query, results) {
        if(size > settings.max_size) {
            flush();
        }

        if(!data[query]) {
            size += 1;
        }

        data[query] = results;
    };

    this.get = function (query) {
        return data[query];
    };
};
}(jQuery));
