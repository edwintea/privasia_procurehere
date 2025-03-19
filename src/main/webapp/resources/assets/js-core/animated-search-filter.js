
    document.addEventListener("DOMContentLoaded", function() {
        "use strict"

        var style = ""
            + "<style>"
            +   "input.animated-search-filter {"
            +     "-webkit-tap-highlight-color: transparent;"
            +   "}"
            +   ".animated-search-filter .hidden {"
            +     "opacity: 0;"
            +     "pointer-events: none;"
            +   "}"
            +   ".animated-search-filter > * {"
            +     "position: absolute;"
            +     "transition: .5s;"
            +   "}"
            + "</style>"

        document.head.insertAdjacentHTML("beforeend", style)

        var items = document.querySelectorAll(".animated-search-filter > *")
        var itemHeight = items[0].offsetHeight
        var texts = []
        var i = -1
        var len = items.length
        var transform = "transform" in document.body.style ? "transform" : "webkitTransform"

        while (++i < len) {
            texts.push(items[i].textContent.trim())
            items[i].style[transform] = "translateY(" + i*itemHeight +"px)"
        }


		$(document).on("oninput","input.animated-search-filter",function(){

            var re = new RegExp(this.value, "i")
            texts.forEach(function(element, index) {
                if (re.test(element)) {
                    items[index].classList.remove("hidden")
                }
                else {
                    items[index].classList.add("hidden")
                }
                var i = -1
                var position = 0
                while (++i < len) {
                    if (items[i].className != "hidden") {
                        items[i].style[transform] = "translateY(" + position++ * itemHeight + "px)"
                    }
                }
            })
        })



    })




    document.addEventListener("DOMContentLoaded", function() {
        "use strict"

        var style = ""
            + "<style>"
            +   "input.animated-search-filter {"
            +     "-webkit-tap-highlight-color: transparent;"
            +   "}"
            +   ".animated-search-filter .hidden {"
            +     "opacity: 0;"
            +     "pointer-events: none;"
            +   "}"
            +   ".animated-search-filter > * {"
            +     "position: absolute;"
            +     "transition: .5s;"
            +   "}"
            + "</style>"

        document.head.insertAdjacentHTML("beforeend", style)

        var items = document.querySelectorAll(".animated-search-filter1 > *")
        var itemHeight = items[0].offsetHeight
        var texts = []
        var i = -1
        var len = items.length
        var transform = "transform" in document.body.style ? "transform" : "webkitTransform"

        while (++i < len) {
            texts.push(items[i].textContent.trim())
            items[i].style[transform] = "translateY(" + i*itemHeight +"px)"
        }


		$(document).on("oninput","input.animated-search-filter1",function(){

            var re = new RegExp(this.value, "i")
            texts.forEach(function(element, index) {
                if (re.test(element)) {
                    items[index].classList.remove("hidden")
                }
                else {
                    items[index].classList.add("hidden")
                }
                var i = -1
                var position = 0
                while (++i < len) {
                    if (items[i].className != "hidden") {
                        items[i].style[transform] = "translateY(" + position++ * itemHeight + "px)"
                    }
                }
            })
        })



    })


    jQuery.fn.highlight = function(pat) {


    function innerHighlight(node, pat) {
        var skip = 0;
        if (node.nodeType == 3) {
            var pos = node.data.toUpperCase().indexOf(pat);

            if (pos >= 0) {
                var spannode = document.createElement('span');
                spannode.className = 'highlight';
                var middlebit = node.splitText(pos);
                var endbit = middlebit.splitText(pat.length);
                var middleclone = middlebit.cloneNode(true);
                spannode.appendChild(middleclone);
                middlebit.parentNode.replaceChild(spannode, middlebit);
                skip = 1;
            }
        }
        else if (node.nodeType == 1 && node.childNodes && !/(script|style)/i.test(node.tagName)) {
            for (var i = 0; i < node.childNodes.length; ++i) {
                i += innerHighlight(node.childNodes[i], pat);
            }
        }
        return skip;
    }
    return this.each(function() {
        innerHighlight(this, pat.toUpperCase());
    });
};

jQuery.fn.removeHighlight = function() {
    function newNormalize(node) {
        for (var i = 0, children = node.childNodes, nodeCount = children.length; i < nodeCount; i++) {
            var child = children[i];
            if (child.nodeType == 1) {
                newNormalize(child);
                continue;
            }
            if (child.nodeType != 3) { continue; }
            var next = child.nextSibling;
            if (next == null || next.nodeType != 3) { continue; }
            var combined_text = child.nodeValue + next.nodeValue;
            new_node = node.ownerDocument.createTextNode(combined_text);
            node.insertBefore(new_node, child);
            node.removeChild(child);
            node.removeChild(next);
            i--;
            nodeCount--;
        }
    }

    return this.find("span.highlight").each(function() {
        var thisParent = this.parentNode;
        thisParent.replaceChild(this.firstChild, this);
        newNormalize(thisParent);
    }).end();
};


