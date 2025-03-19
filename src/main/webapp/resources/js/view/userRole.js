// checkCheckedboxs();
/*$(document).delegate('.nvigator+input[type="checkbox"]', 'change', function() {
  	if ($(this).prop('checked') == false) {
  		//console.log('>> Unchecked : ' + $(this).val());
  		//$(this).parent().removeClass('checked');
  		//$(this).parents('div').siblings('ul').find('input[type="checkbox"]').prop('checked', false);
  		$(this).siblings('ul').find('input[type="checkbox"]').prop('checked', false);
  		/*$(this).parents('div').siblings('ul').find('input[type="checkbox"]').parent().removeClass('checked');
  		if ($(this).parent().parent().parent().parent().find('input[type="checkbox"]:checked').length == 0) {
  			$(this).parents('ul').siblings('div').children('span').removeClass('checked');
  			$(this).parents('ul').siblings('div').children('span').children('input[type="checkbox"]').prop('checked', false);
  		}

  		if ($(this).closest('ul').siblings('ul').find('input[type="checkbox"]:checked').length == 0){
  			$(this).siblings('ul').find('input[type="checkbox"]').prop('checked', false);
  			$(this).closest('ul').siblings('input[type=checkbox]').prop('checked', false);

  		}

  		if ($(this).parents('ul').siblings('ul').find('input[type="checkbox"]:checked').length == 0)
  		{
  		$(this).parents('ul').siblings('input[type=checkbox]').prop('checked', false);
  		}

  	} else {
  		if($(this).val() == 'ROLE_ADMIN_READONLY') {
  			console.log('ADMIN READONLY CHECKED !!! : ' + $(this).val());
  			$(this).parents('ul').find('input[type=checkbox]').each(function(){
  				if($(this).val() != 'ROLE_USER') {
  					$(this).prop('checked', false)
  				}
  			});
  			$(this).prop('checked', true);
  		} else {

  			$(this).siblings('ul').find('input[type="checkbox"]').prop('checked', true);
  			$(this).parents('ul').siblings('input[type=checkbox]').prop('checked', true);
  		}
  		//$(this).parent().addClass('checked');
  		//$(this).parents('div').siblings('ul').find('input[type="checkbox"]').prop('checked', true);
  		//$(this).parents('div').siblings('ul').find('input[type="checkbox"]').parent().addClass('checked');
  		//$(this).parents('ul').siblings('div').children('span').addClass('checked');
  		//$(this).parents('ul').siblings('div').children('span').children('input[type="checkbox"]').prop('checked', true);
  	}
  });*/

//4105
// Existing function for handling checkbox changes
$(document).delegate('.nvigator+input[type="checkbox"]', 'change', function() {
    var isChecked = $(this).is(':checked');
    console.log("what is checked  " + $(this).val());
    if (userHasSupplierRole) {
        // Execute code for supplier
        console.log("User has the SUPPLIER role.");
        //checking checkboxes
        if ($(this).val() === 'ROLE_PROC_TO_PAY') {
            console.log('Procure-to-Pay is checked:', isChecked);

            var checkboxIds = ['#accessControlList14', '#accessControlList15', '#accessControlList17','#accessControlList18', '#accessControlList20', '#accessControlList21', '#accessControlList22', '#accessControlList23', '#accessControlList25', '#accessControlList26'];

            checkboxIds.forEach(function(id) {
                $(id).prop('checked', isChecked);
            });
        }
        else if ($(this).val() === 'ROLE_ACCEPT_PO') {
                     console.log('ROLE_ACCEPT_PO', isChecked);

                     var checkboxIds = ['#accessControlList14', '#accessControlList15'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList15') {
                                console.log(" for supplier");
                             if (isChecked) {
                                 // Disable #accessControlList15 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList15 and uncheck it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                else if (id === '#accessControlList14') {
                        if (isChecked) {
                            // Disable #accessControlList83 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList83 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                         else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }
         else if ($(this).val() === 'ROLE_PR_PO') {
                console.log('ROLE_PR_PO', isChecked);

                var checkboxIds = ['#accessControlList14', '#accessControlList15', '#accessControlList16'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList15') {
                        if (isChecked) {
                            // Disable #accessControlList81 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList81 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                    else if (id === '#accessControlList16') {
                    // do not auto check #accessControlList85
                            $(id).prop('checked', false);
                    }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }

        else if ($(this).val() === 'ROLE_VIEW_PO_LIST') {
                     console.log('ROLE_VIEW_PO_LIST supplier', isChecked);
                     var checkboxIds = ['#accessControlList14'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_PAYMENT_RECORD') {
                     console.log('ROLE_VIEW_PAYMENT_RECORD supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList25'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_CREATE_DO') {
                     console.log('ROLE_CREATE_DO', isChecked);

                     var checkboxIds = ['#accessControlList17', '#accessControlList18'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList18') {
                                console.log(" for supplier");
                             if (isChecked) {
                                 // Disable #accessControlList15 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList15 and uncheck it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                else if (id === '#accessControlList17') {
                        if (isChecked) {
                            // Disable #accessControlList83 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList83 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                         else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_DO_LIST') {
                     console.log('ROLE_VIEW_DO_LIST Supplier', isChecked);
                     var checkboxIds = ['#accessControlList17'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
         else if ($(this).val() === 'ROLE_DO_INVOICE') {
                console.log('ROLE_DO_INVOICE supplier', isChecked);

                var checkboxIds = ['#accessControlList17', '#accessControlList18','#accessControlList19'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList18') {
                        if (isChecked) {
                            // Disable #accessControlList87 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList87 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                     else if(id === '#accessControlList19'){
                                 $(id).prop('checked', false);
                     }
                     else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
        else if ($(this).val() === 'ROLE_GRN_VIEW_ONLY') {
                     console.log('ROLE_GRN_VIEW_ONLY', isChecked);
                     var checkboxIds = ['#accessControlList20'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_CN_DN') {
                     console.log('ROLE_VIEW_CN_DN supplier', isChecked);
                     var checkboxIds = ['#accessControlList22'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
         else if ($(this).val() === 'ROLE_Invoice_CN_DN') {
                console.log('ROLE_Invoice_CN_DN', isChecked);

                var checkboxIds = ['#accessControlList22', '#accessControlList23', '#accessControlList24'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList23') {
                        if (isChecked) {
                            // Disable #accessControlList23 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList23 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                  else if(id === '#accessControlList24')    {
                             $(id).prop('checked', false);
                  }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
        else if ($(this).val() === 'ROLE_CREATE_CN_DN') {
                     console.log('ROLE_CREATE_CN_DN', isChecked);

                     var checkboxIds = ['#accessControlList22', '#accessControlList23'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList23') {
                                console.log(" for supplier");
                             if (isChecked) {
                                 // Disable #accessControlList15 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList15 and uncheck it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                else if (id === '#accessControlList22') {
                        if (isChecked) {
                            // Disable #accessControlList83 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList83 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                         else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }
        else if($(this).val() == 'ROLE_ADMIN_READONLY') {
                console.log('ADMIN READONLY CHECKED !!! : ' + $(this).val());
                $(this).parents('ul').find('input[type=checkbox]').each(function(){
                    if($(this).val() != 'ROLE_USER') {
                        $(this).prop('checked', false)
                    }
                });
                //$(this).prop('checked', true);

                if (isChecked) {
                // Disable #accessControlList15 and ensure it is checked
                  $(this).prop('checked', true);
                 } else {
                 // Re-enable #accessControlList15 and uncheck it
                $(this).prop('checked', false);
                }
            }

         else {
            if (isChecked) {
                $(this).siblings('ul').find('input[type="checkbox"]').prop('checked', true);
            } else {
                $(this).siblings('ul').find('input[type="checkbox"]').prop('checked', false);
            }
        }
// end of checkbox checking
    }

    if (userHasBuyerRole) {
        // Execute code for buyer
        console.log("User has the BUYER role.");

    //checking checkboxes

if ($(this).val() === 'ROLE_PROC_TO_PAY') {
    console.log('Procure-to-Pay is checked:', isChecked);

    var checkboxIds = ['#accessControlList80', '#accessControlList81', '#accessControlList83', '#accessControlList84', '#accessControlList86', '#accessControlList87', '#accessControlList89', '#accessControlList93', '#accessControlList90', '#accessControlList92', '#accessControlList95', '#accessControlList96', '#accessControlList97', '#accessControlList98'];
    var checkboxIds2 = ['#accessControlList82', '#accessControlList85', '#accessControlList88', '#accessControlList91', '#accessControlList94', '#accessControlList99'];

    // Set all checkboxes to match the state of ROLE_PROC_TO_PAY
    checkboxIds.forEach(function(id) {
        $(id).prop('checked', isChecked).prop('disabled', false);
    });

    // Uncheck all when Procure-to-Pay is unchecked
    if (!isChecked) {
        checkboxIds2.forEach(function(id) {
            $(id).prop('checked', false);
        });
    }
}
    else if ($(this).val() === 'ROLE_PR_CREATE') {
        var checkboxIds = ['#accessControlList80', '#accessControlList81'];

        checkboxIds.forEach(function(id) {
            if (id === '#accessControlList81') {
                if (isChecked) {
                    // Disable #accessControlList81 and ensure it is checked
                    $(id).prop('checked', true).prop('disabled', true);
                } else {
                    // Re-enable #accessControlList81 and uncheck it
                    $(id).prop('checked', true).prop('disabled', false);
                }
            }
            else if (id === '#accessControlList80') {
                        if (isChecked) {
                            // Disable #accessControlList80 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList80 and uncheck it
                            $(id).prop('checked', true);
                        }
            }
            else {
                console.log("Processing other checkboxes");
                $(id).prop('checked', isChecked).prop('disabled', false);
            }
        });
    }
    else if ($(this).val() === 'ROLE_PO_CREATE') {
             console.log('ROLE_PO_CREATE', isChecked);

             var checkboxIds = ['#accessControlList83', '#accessControlList84'];
             checkboxIds.forEach(function(id) {
                 if (id === '#accessControlList84') {
                     if (isChecked) {
                         // Disable #accessControlList84 and ensure it is checked
                         $(id).prop('checked', true).prop('disabled', true);
                     } else {
                         // Re-enable #accessControlList84 and uncheck it
                         $(id).prop('checked', true).prop('disabled', false);
                     }
                 }
                else if (id === '#accessControlList83') {
                        if (isChecked) {
                            // Disable #accessControlList83 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList83 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                 else {
                     console.log("Processing other checkboxes");
                     $(id).prop('checked', isChecked).prop('disabled', false);
                 }
             });
         }

        else if ($(this).val() === 'ROLE_ACCEPT_DO') {
                     console.log('ROLE_ACCEPT_DO', isChecked);

                     var checkboxIds = ['#accessControlList86', '#accessControlList87'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList87') {
                                console.log(" for buyer");
                             if (isChecked) {
                                 // Disable #accessControlList87 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList87 and uncheck it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                    else if (id === '#accessControlList86') {
                        if (isChecked) {
                            // Disable #accessControlList86 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList86 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                          else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }
        else if ($(this).val() === 'ROLE_GRN_EDIT') {
                     console.log('ROLE_GRN_EDIT', isChecked);

                     var checkboxIds = ['#accessControlList89', '#accessControlList90'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList90') {
                                console.log(" for buyer");
                             if (isChecked) {
                                 // Disable #accessControlList15 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList15 and check it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                    else if (id === '#accessControlList89') {
                        if (isChecked) {
                            // Disable #accessControlList89 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList89 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }

                         else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }
        else if ($(this).val() === 'ROLE_ACCEPT_CN_DN') {
                     console.log("ROLE_ACCEPT_CN_DN "+ isChecked);
                     var checkboxIds = ['#accessControlList92', '#accessControlList93'];
                     checkboxIds.forEach(function(id) {
                         if (id === '#accessControlList93') {
                                console.log(" for buyer");
                             if (isChecked) {
                                 // Disable #accessControlList15 and ensure it is checked
                                 $(id).prop('checked', true).prop('disabled', true);
                             } else {
                                 // Re-enable #accessControlList15 and maintain check it
                                 $(id).prop('checked', true).prop('disabled', false);
                             }
                         }
                    else if (id === '#accessControlList92') {
                        if (isChecked) {
                            // Disable #accessControlList92 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList92 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }
                          else {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                         }
                     });
        }

        else if ($(this).val() === 'ROLE_VIEW_AP') {
                     console.log('ROLE_VIEW_AP', isChecked);
                     var checkboxIds = ['#accessControlList95'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_PAYMENT_RECORD') {
                     console.log('ROLE_VIEW_PAYMENT_RECORD supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList97'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_PR_DRAFT') {
                     console.log('ROLE_VIEW_PR_DRAFT supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList80'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_PO_LIST') {
                     console.log('ROLE_VIEW_PO_LIST supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList83'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_DO_LIST') {
                     console.log('ROLE_VIEW_DO_LIST supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList86'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_GRN_VIEW_ONLY') {
                     console.log('ROLE_GRN_VIEW_ONLY supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList89'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
        else if ($(this).val() === 'ROLE_VIEW_CN_DN') {
                     console.log('ROLE_VIEW_CN_DN supplier & buyer', isChecked);
                     var checkboxIds = ['#accessControlList92'];
                     checkboxIds.forEach(function(id) {
                             console.log("Processing other checkboxes");
                             $(id).prop('checked', isChecked).prop('disabled', false);
                     });
        }
         else if ($(this).val() === 'ROLE_CREATE_PAYMENT_RECORD') {
                console.log('ROLE_CREATE_PAYMENT_RECORD', isChecked);

                var checkboxIds = ['#accessControlList97', '#accessControlList98'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList98') {
                        if (isChecked) {
                            // Disable #accessControlList98 and ensure it is checked
                            $(id).prop('checked', true).prop('disabled', true);
                        } else {
                            // Re-enable #accessControlList98 and uncheck it
                            $(id).prop('checked', true).prop('disabled', false);
                        }
                    }
                    else if (id === '#accessControlList97') {
                        if (isChecked) {
                            // Disable #accessControlList97 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList98 and uncheck it
                            $(id).prop('checked', true);
                        }
                    }

                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_PURCHASE_REQUISITION') {
                console.log('ROLE_PURCHASE_REQUISITION', isChecked);

                var checkboxIds = ['#accessControlList80', '#accessControlList81','#accessControlList82'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList81') {
                        if (isChecked) {
                            // Disable #accessControlList81 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList81 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                   else if (id === '#accessControlList82') {
                            // do not auto check #accessControlList82
                            $(id).prop('checked', false);
                    }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_PR_PO') {
                console.log('ROLE_PR_PO', isChecked);

                var checkboxIds = ['#accessControlList83', '#accessControlList84', '#accessControlList85'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList84') {
                        if (isChecked) {
                            // Disable #accessControlList81 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList81 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                    else if (id === '#accessControlList85') {
                    // do not auto check #accessControlList85
                            $(id).prop('checked', false);
                    }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_DO_INVOICE') {
                console.log('ROLE_DO_INVOICE', isChecked);

                var checkboxIds = ['#accessControlList86', '#accessControlList87','#accessControlList88'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList87') {
                        if (isChecked) {
                            // Disable #accessControlList87 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList87 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                     else if(id === '#accessControlList88'){
                                 $(id).prop('checked', false);
                     }
                     else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_GRN_LIST') {
                console.log('ROLE_GRN_LIST', isChecked);

                var checkboxIds = ['#accessControlList89', '#accessControlList90', '#accessControlList91'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList90') {
                        if (isChecked) {
                            // Disable #accessControlList90 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList90 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                 else if(id === '#accessControlList91')    {
                            $(id).prop('checked', false);
                 }
                     else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_Invoice_CN_DN') {
                console.log('ROLE_Invoice_CN_DN', isChecked);

                var checkboxIds = ['#accessControlList92', '#accessControlList93', '#accessControlList94'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList93') {
                        if (isChecked) {
                            // Disable #accessControlList93 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList93 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                  else if(id === '#accessControlList94')    {
                             $(id).prop('checked', false);
                  }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
         else if ($(this).val() === 'ROLE_PAYMENT_RECORD') {
                console.log('ROLE_PAYMENT_RECORD', isChecked);

                var checkboxIds = ['#accessControlList97', '#accessControlList98', '#accessControlList99'];

                checkboxIds.forEach(function(id) {
                    if (id === '#accessControlList98') {
                        if (isChecked) {
                            // Disable #accessControlList98 and ensure it is checked
                            $(id).prop('checked', true);
                        } else {
                            // Re-enable #accessControlList98 and uncheck it
                            $(id).prop('checked', false).prop('disabled', false);
                        }
                    }
                  else if(id === '#accessControlList99')    {
                             $(id).prop('checked', false);
                  }
                    else {
                        console.log("Processing other checkboxes");
                        $(id).prop('checked', isChecked).prop('disabled', false);
                    }
                });
            }
        else if($(this).val() == 'ROLE_ADMIN_READONLY') {
                console.log('ADMIN READONLY CHECKED !!! : ' + $(this).val());
                $(this).parents('ul').find('input[type=checkbox]').each(function(){
                    if($(this).val() != 'ROLE_USER') {
                        $(this).prop('checked', false)
                    }
                });
                //$(this).prop('checked', true);

                if (isChecked) {
                // checked #accessControlList15 and ensure it is checked
                  $(this).prop('checked', true);
                 } else {
                 // Re-enable #accessControlList15 and uncheck it
                $(this).prop('checked', false);
                }
            }


     else {
        if (isChecked) {
            $(this).siblings('ul').find('input[type="checkbox"]').prop('checked', true);
        } else {
            $(this).siblings('ul').find('input[type="checkbox"]').prop('checked', false);
        }
    }
// end of checkbox checking
 }
    //console.log("Checkboxes inside sibling UL:", $(this).siblings('ul').find('input[type="checkbox"]'));
    console.log("Number of checkboxes found:", $(this).siblings('ul').find('input[type="checkbox"]').length);
});

// Re-enable disabled checkboxes on form submission
$('form').on('submit', function() {
    $('#accessControlList81').prop('disabled', false);
    $('#accessControlList15').prop('disabled', false);
    $('#accessControlList84').prop('disabled', false);
    $('#accessControlList87').prop('disabled', false);
    $('#accessControlList90').prop('disabled', false);
    $('#accessControlList93').prop('disabled', false);
    $('#accessControlList98').prop('disabled', false);
    $('#accessControlList18').prop('disabled', false);
    $('#accessControlList20').prop('disabled', false);
    $('#accessControlList23').prop('disabled', false);
});

//function checkCheckedboxs() {
//	$('.checkboxesRole:checked').parent().addClass('checked');
//}

$(document).on('click',
		'.nvigator',
				function() {
					var obj1 = $(this);
					var appenHtm = $(this).parent();
					var parentvalue = $(this).next().val();
					var fieldLabelName = $(this).next().attr('name');
					//appenHtm.find('ul').remove();
					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");
					var aclValue = $(this).next().children('span').children('input[type="checkbox"]').val();
					$(this).next().children('span').children('input[type="checkbox"]').change();

					if ($(obj1).find('i').hasClass('fa-plus')) {
						$(obj1).find('i').removeClass('fa-plus fa-spinner').addClass('fa-minus');
						appenHtm.find('ul').show();
					} else {
						//$(obj1).parent('li').find('ul').slideToggle('slow');
						$(obj1).find('i').removeClass('fa-minus').addClass('fa-plus');
						appenHtm.find('ul').hide();
					}
				});

loadAllCheckboxValues();

function loadAllCheckboxValues() {
    //console.log("loadAllCheckboxValues");
	$('.leftSideOfCheckbox').each(function() {
		var currentleftBlock = $(this);
		currentleftBlock.next('.rightSideOfCheckbox').html('');
		currentleftBlock.find('input[type="checkbox"]:checked').each(function() {
			var htmldata = '<div class="item" data-value="' + $(this).val() + '"><span class="remove-selected">×</span>' + $(this).siblings(".number").text() + '</div>';
			$(this).closest('.chk_scroll_box').find('.rightSideOfCheckbox').append(htmldata);
		});
	});
}
    $(document).on('change', '.leftSideOfCheckbox input[type=checkbox]', function() {
        $(this).parents('ul').siblings('input[type=checkbox]').prop('checked', true);
        $(this).closest('.chk_scroll_box').find('.rightSideOfCheckbox').html('');
        $(this).closest('.leftSideOfCheckbox').find('input[type="checkbox"]:checked').each(function() {
            var htmldata = '<div class="item" data-value="' + $(this).val() + '"><span class="remove-selected">×</span>' + $(this).siblings(".number").text() + '</div>';
            $(this).closest('.chk_scroll_box').find('.rightSideOfCheckbox').append(htmldata);
        });
    });

    document.addEventListener('DOMContentLoaded', function() {

        // Function to check the state of checkboxes
        function checkAdminAndAppUser() {
            var isAdminChecked = document.getElementById('checkbox_ROLE_ADMIN').checked;
            var isAppUserChecked = document.getElementById('checkbox_ROLE_USER').checked;
            //var isBuyerChecked = document.getElementById('checkbox_ROLE_BUYER_LIST').checked;

            //console.log("!!!!! isBuyerChecked "+isBuyerChecked);
            // Assuming the ID of the newly added checkbox is 'checkbox_NEW_ROLE'
            if (isAdminChecked && isAppUserChecked) {
               //document.getElementById('checkbox_ROLE_PROC_TO_PAY').checked = true;  // Check the new checkbox
               if(userHasBuyerRole){
               console.log("User has the BUYER role.");
                           var isRolePrChecked = document.getElementById('accessControlList80').checked;
                           var isCreatePrChecked = document.getElementById('accessControlList82').checked;
                           var isViewPrChecked = document.getElementById('accessControlList81').checked;
                           var isRolePoChecked = document.getElementById('accessControlList83').checked;
                           var isViewPoChecked = document.getElementById('accessControlList84').checked;
                           var isCreatePoChecked = document.getElementById('accessControlList85').checked;
                           var isRoleDoChecked = document.getElementById('accessControlList86').checked;
                           var isViewDoChecked = document.getElementById('accessControlList87').checked;
                           var isAcceptDoChecked = document.getElementById('accessControlList88').checked;
                           var isCreateGRChecked = document.getElementById('accessControlList91').checked;
                           var isRoleGRChecked = document.getElementById('accessControlList89').checked;
                           var isViewGRChecked = document.getElementById('accessControlList90').checked;
                           var isAcceptCnChecked = document.getElementById('accessControlList94').checked;
                           var isViewCnChecked = document.getElementById('accessControlList93').checked;
                           var isRoleCnChecked = document.getElementById('accessControlList92').checked;
                           var isCreatePymtChecked = document.getElementById('accessControlList99').checked;
                           var isRolePymtChecked = document.getElementById('accessControlList97').checked;
                           var isViewPymtChecked = document.getElementById('accessControlList98').checked;
            /*               //PR Buyer Admin
                           document.getElementById('accessControlList80').checked = true;
                           document.getElementById('accessControlList81').checked = true;
                          //PO Buyer Admin
                           document.getElementById('accessControlList83').checked = true;
                           document.getElementById('accessControlList84').checked = true;
                           //DO Buyer Admin
                           document.getElementById('accessControlList86').checked = true;
                           document.getElementById('accessControlList87').checked = true;
                           //gr Buyer Admin
                           document.getElementById('accessControlList89').checked = true;
                           document.getElementById('accessControlList90').checked = true;
                           //invoice Buyer Admin
                           document.getElementById('accessControlList92').checked = true;
                           document.getElementById('accessControlList93').checked = true;
                           //AP Buyer Admin
                           document.getElementById('accessControlList95').checked = true;
                           document.getElementById('accessControlList96').checked = true;
                           //payment rec Buyer Admin
                           document.getElementById('accessControlList97').checked = true;
                           document.getElementById('accessControlList98').checked = true;*/
                            if(isCreatePrChecked==true){
                                       //PR Buyer Non admin
                                                  document.getElementById('accessControlList80').checked = true;
                                                  document.getElementById('accessControlList81').checked = true;
                                        // Disable "View PR List" to prevent changes
                                                document.getElementById('accessControlList81').disabled = true;
                                       }
                                       if(isCreatePoChecked==true){
                                       //po Buyer Non admin
                                                  document.getElementById('accessControlList83').checked = true;
                                                  document.getElementById('accessControlList84').checked = true;
                                        // Disable "View po List" to prevent changes
                                                document.getElementById('accessControlList84').disabled = true;
                                       }
                                       if(isViewPrChecked==true){
                                       //PR View Non admin
                                                  document.getElementById('accessControlList80').checked = true;
                                       }
                                       if(isViewPoChecked==false){
                                       //PR View Non admin
                                                  document.getElementById('accessControlList83').checked = false;
                                       }

                                       if(isAcceptDoChecked==true){
                                       //do Buyer Non admin
                                                  document.getElementById('accessControlList86').checked = true;
                                                  document.getElementById('accessControlList87').checked = true;
                                        // Disable "View DO List" to prevent changes
                                                document.getElementById('accessControlList87').disabled = true;
                                       }
                                       if(isCreateGRChecked==true){
                                       //GR Buyer Non admin
                                                  document.getElementById('accessControlList89').checked = true;
                                                  document.getElementById('accessControlList90').checked = true;
                                        // Disable "View GR List" to prevent changes
                                                document.getElementById('accessControlList90').disabled = true;
                                       }
                                       if(isAcceptCnChecked==true){
                                       //CN Buyer Non admin
                                                  document.getElementById('accessControlList92').checked = true;
                                                  document.getElementById('accessControlList93').checked = true;
                                        // Disable "View CN List" to prevent changes
                                                document.getElementById('accessControlList93').disabled = true;
                                       }
                                       if(isCreatePymtChecked==true){
                                       //Payment record Buyer Non admin
                                                  document.getElementById('accessControlList97').checked = true;
                                                  document.getElementById('accessControlList98').checked = true;
                                        // Disable "View Payment Record List" to prevent changes
                                                document.getElementById('accessControlList98').disabled = true;
                                       }
                                       if(isViewPymtChecked==true && isCreatePymtChecked==false){
                                       //Payment record Buyer Non admin
                                                  document.getElementById('accessControlList97').checked = true;
                                       }
                }
               if(userHasSupplierRole){
                console.log("User has the SUPPLIER role.");
               //PO SUPPLIER Admin
               document.getElementById('accessControlList14').checked = true;
               document.getElementById('accessControlList15').checked = true;
              //DO SUPPLIER Admin
               document.getElementById('accessControlList17').checked = true;
               document.getElementById('accessControlList18').checked = true;
               //gr SUPPLIER Admin
               document.getElementById('accessControlList20').checked = true;
               document.getElementById('accessControlList21').checked = true;
               //invoice SUPPLIER Admin
               document.getElementById('accessControlList22').checked = true;
               document.getElementById('accessControlList23').checked = true;
               //payment rec SUPPLIER Admin
               document.getElementById('accessControlList25').checked = true;
               document.getElementById('accessControlList26').checked = true;

                }
            } else {
            console.log("non admin here?");
             if(userHasBuyerRole){
            var isRolePrChecked = document.getElementById('accessControlList80').checked;
            var isCreatePrChecked = document.getElementById('accessControlList82').checked;
            var isViewPrChecked = document.getElementById('accessControlList81').checked;
            var isRolePoChecked = document.getElementById('accessControlList83').checked;
            var isViewPoChecked = document.getElementById('accessControlList84').checked;
            var isCreatePoChecked = document.getElementById('accessControlList85').checked;
            var isRoleDoChecked = document.getElementById('accessControlList86').checked;
            var isViewDoChecked = document.getElementById('accessControlList87').checked;
            var isAcceptDoChecked = document.getElementById('accessControlList88').checked;
            var isCreateGRChecked = document.getElementById('accessControlList91').checked;
            var isRoleGRChecked = document.getElementById('accessControlList89').checked;
            var isViewGRChecked = document.getElementById('accessControlList90').checked;
            var isAcceptCnChecked = document.getElementById('accessControlList94').checked;
            var isViewCnChecked = document.getElementById('accessControlList93').checked;
            var isRoleCnChecked = document.getElementById('accessControlList92').checked;
            var isCreatePymtChecked = document.getElementById('accessControlList99').checked;
            var isRolePymtChecked = document.getElementById('accessControlList97').checked;
            var isViewPymtChecked = document.getElementById('accessControlList98').checked;
            //console.log("do u detect isRolePymtChecked>>>>> "+isRolePymtChecked);
            if(isCreatePrChecked==true){
            //PR Buyer Non admin
                       document.getElementById('accessControlList80').checked = true;
                       document.getElementById('accessControlList81').checked = true;
             // Disable "View PR List" to prevent changes
                     document.getElementById('accessControlList81').disabled = true;
            }
            if(isCreatePoChecked==true){
            //po Buyer Non admin
                       document.getElementById('accessControlList83').checked = true;
                       document.getElementById('accessControlList84').checked = true;
             // Disable "View po List" to prevent changes
                     document.getElementById('accessControlList84').disabled = true;
            }
            if(isViewPrChecked==true){
            //PR View Non admin
                       document.getElementById('accessControlList80').checked = true;
            }
            if(isViewPoChecked==false){
            //PR View Non admin
                       document.getElementById('accessControlList83').checked = false;
            }

            if(isAcceptDoChecked==true){
            //do Buyer Non admin
                       document.getElementById('accessControlList86').checked = true;
                       document.getElementById('accessControlList87').checked = true;
             // Disable "View DO List" to prevent changes
                     document.getElementById('accessControlList87').disabled = true;
            }
            if(isCreateGRChecked==true){
            //GR Buyer Non admin
                       document.getElementById('accessControlList89').checked = true;
                       document.getElementById('accessControlList90').checked = true;
             // Disable "View GR List" to prevent changes
                     document.getElementById('accessControlList90').disabled = true;
            }
            if(isAcceptCnChecked==true){
            //CN Buyer Non admin
                       document.getElementById('accessControlList92').checked = true;
                       document.getElementById('accessControlList93').checked = true;
             // Disable "View CN List" to prevent changes
                     document.getElementById('accessControlList93').disabled = true;
            }
            if(isCreatePymtChecked==true){
            //Payment record Buyer Non admin
                       document.getElementById('accessControlList97').checked = true;
                       document.getElementById('accessControlList98').checked = true;
             // Disable "View Payment Record List" to prevent changes
                     document.getElementById('accessControlList98').disabled = true;
            }
            if(isViewPymtChecked==true && isCreatePymtChecked==false){
            //Payment record Buyer Non admin
                       document.getElementById('accessControlList97').checked = true;
            }
                //document.getElementById('checkbox_ROLE_PROC_TO_PAY').checked = false;  // Uncheck if either one is unchecked
            }
           }
        }

        // Attach event listeners to Admin and Application User checkboxes
        document.getElementById('checkbox_ROLE_ADMIN').addEventListener('change', checkAdminAndAppUser);
        document.getElementById('checkbox_ROLE_USER').addEventListener('change', checkAdminAndAppUser);

        // Call the function on page load in case both are already checked
        checkAdminAndAppUser();
    });
    function toggleProcureToPayAccess() {
        const procureToPayCheckbox = document.getElementById('checkbox_ROLE_PROC_TO_PAY');
        const createPrCheckbox = document.getElementById('accessControlList82');
        const viewPrListCheckbox = document.getElementById('accessControlList81');

        if (procureToPayCheckbox.checked) {
            // Enable related checkboxes when Procure-to-Pay is checked
            createPrCheckbox.checked = false; // Set Create PR checkbox as checked
            viewPrListCheckbox.checked = true; // Ensure View PR List is not pre-checked
            viewPrListCheckbox.disabled = false; // Enable View PR List
        } else {
            // When Procure-to-Pay is unticked, uncheck and disable related checkboxes
            createPrCheckbox.checked = false; // Uncheck Create PR
            viewPrListCheckbox.checked = false; // Uncheck View PR List
            viewPrListCheckbox.disabled = false; // Disable View PR List (greyed out)

            // Uncheck all other child and subchild checkboxes related to Procure-to-Pay
            const allChildCheckboxes = document.querySelectorAll('.procure-to-pay-child');
            allChildCheckboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
        }
    }

    function toggleProcureToPayAccessSup() {
            const procureToPayCheckbox = document.getElementById('checkbox_ROLE_PROC_TO_PAY');
            const createPoCheckbox = document.getElementById('accessControlList16');
            const viewPoListCheckbox = document.getElementById('accessControlList15');
            const viewDoListCheckbox = document.getElementById('accessControlList18');
            const createDoListCheckbox = document.getElementById('accessControlList19');
            const viewInvListCheckbox = document.getElementById('accessControlList23');
            const createInvListCheckbox = document.getElementById('accessControlList24');

            if (procureToPayCheckbox.checked) {
                // Enable related checkboxes when Procure-to-Pay is checked
                createPoCheckbox.checked = false; // Set Create Po checkbox as checked
                viewPoListCheckbox.checked = true; // Ensure View Po List is not pre-checked
                viewPoListCheckbox.disabled = false; // Enable View Po List
            } else {
                // When Procure-to-Pay is unticked, uncheck and disable related checkboxes
                createPoCheckbox.checked = false; // Uncheck Create Po
                viewPoListCheckbox.checked = false; // Uncheck View Po List
                viewPoListCheckbox.disabled = false; // Disable View Po List (greyed out)

                createDoListCheckbox.checked = false; // Uncheck Create do
                viewDoListCheckbox.checked = false; // Uncheck View do List
                viewDoListCheckbox.disabled = false; // Disable View do List (greyed out)

                createInvListCheckbox.checked = false; // Uncheck Create inv
                viewInvListCheckbox.checked = false; // Uncheck View inv List
                viewInvListCheckbox.disabled = false; // Disable View inv List (greyed out)
                // Uncheck all other child and subchild checkboxes related to Procure-to-Pay
                const allChildCheckboxes = document.querySelectorAll('.procure-to-pay-child');
                allChildCheckboxes.forEach(checkbox => {
                    checkbox.checked = false;
                });
            }
        }

     document.addEventListener("DOMContentLoaded", function() {
            // For supplier role
            if (userHasSupplierRole) {
                console.log("proc to pay supplier");
                document.getElementById('checkbox_ROLE_PROC_TO_PAY').addEventListener('change', toggleProcureToPayAccessSup);
         }
          if (userHasBuyerRole) {
          console.log(" BUYER p2p ");
         document.getElementById('checkbox_ROLE_PROC_TO_PAY').addEventListener('change', toggleProcureToPayAccess);
      }
    });

function toggleUncheckP2P() {
    const procureToPayCheckbox = document.getElementById('checkbox_ROLE_PROC_TO_PAY');
    const mainPrCheckbox = document.getElementById('accessControlList80');
    const viewPrListCheckbox = document.getElementById('accessControlList81');
    const mainPoCheckbox = document.getElementById('accessControlList83'); // New checkbox
    const viewPoCheckbox = document.getElementById('accessControlList84'); // New checkbox
    const mainDoCheckbox = document.getElementById('accessControlList86'); // New checkbox
    const viewDoCheckbox = document.getElementById('accessControlList87'); // New checkbox
    const mainGrCheckbox = document.getElementById('accessControlList89'); // New checkbox
    const viewGrCheckbox = document.getElementById('accessControlList90'); // New checkbox
    const mainInvCheckbox = document.getElementById('accessControlList92'); // New checkbox
    const viewInvCheckbox = document.getElementById('accessControlList93'); // New checkbox
    const mainApCheckbox = document.getElementById('accessControlList95'); // New checkbox
    const viewApCheckbox = document.getElementById('accessControlList96'); // New checkbox
    const mainPymtCheckbox = document.getElementById('accessControlList97'); // New checkbox
    const viewPymtCheckbox = document.getElementById('accessControlList98'); // New checkbox
    var checkboxIds = ['#accessControlList80', '#accessControlList81', '#accessControlList83', '#accessControlList84', '#accessControlList86', '#accessControlList87',
    '#accessControlList89','#accessControlList90', '#accessControlList93', '#accessControlList92', '#accessControlList95', '#accessControlList96',
     '#accessControlList97', '#accessControlList98'];

    // Logic for viewPrListCheckbox (accessControlList81)
    if (!mainPrCheckbox.checked || !viewPrListCheckbox.checked) {
        console.log("testing when untick default PR");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList80' && id !== '#accessControlList81');
    }

    // Logic for viewPoListCheckbox (accessControlList84)
    if (!mainPoCheckbox.checked || !viewPoCheckbox.checked) {
        console.log("testing when untick PO ");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList83'&& id !== '#accessControlList84');
    }
    // Logic for viewDoListCheckbox (accessControlList86)
    if (!mainDoCheckbox.checked || !viewDoCheckbox.checked) {
        console.log("testing when untick accessControlList86");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList86'&& id !== '#accessControlList87');
    }

    // Logic for mainGrCheckbox (accessControlList89)
    if (!mainGrCheckbox.checked || !viewGrCheckbox.checked) {
        console.log("testing when untick accessControlList89");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList89'&& id !== '#accessControlList90');
    }
    // Logic for mainInvCheckbox (accessControlList92)
    if (!mainInvCheckbox.checked || !viewInvCheckbox.checked) {
        console.log("testing when untick accessControlList92");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList92'&& id !== '#accessControlList93');
    }
    // Logic for mainApCheckbox (accessControlList95)
    if (!mainApCheckbox.checked || !viewApCheckbox.checked) {
        console.log("testing when untick accessControlList95");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList95'&& id !== '#accessControlList96');
    }
    // Logic for mainPymtCheckbox (accessControlList97)
    if (!mainPymtCheckbox.checked || !viewPymtCheckbox.checked) {
        console.log("testing when untick accessControlList97");
        checkboxIds = checkboxIds.filter(id => id !== '#accessControlList97'&& id !== '#accessControlList98');
    }

    // After filtering, check if the array is empty and uncheck procureToPayCheckbox if needed
    console.log("checkboxIds.length>>>>>> "+checkboxIds.length);
    if (checkboxIds.length === 0) {
        console.log("No more checkboxes to check, unchecking Procure-to-Pay");
        procureToPayCheckbox.checked = false; // Uncheck Procure-to-Pay
    }
    else if (checkboxIds.length >= 11) {
        console.log("No more checkboxes to check, unchecking Procure-to-Pay");
        procureToPayCheckbox.checked = true; // Uncheck Procure-to-Pay
    }
    else if (checkboxIds.length <= 13) {
        console.log("No more checkboxes to check, unchecking Procure-to-Pay");
        procureToPayCheckbox.checked = false; // Uncheck Procure-to-Pay
    }
    //console.log("hello array>>>>>> "+checkboxIds);  // Check the modified array
}

/*document.getElementById('accessControlList80').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList81').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList83').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList84').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList86').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList87').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList89').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList90').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList92').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList93').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList95').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList96').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList97').addEventListener('change', toggleUncheckP2P);
document.getElementById('accessControlList98').addEventListener('change', toggleUncheckP2P);*/

    function toggleUncheckP2PSupplier() {

        const procureToPayCheckbox = document.getElementById('checkbox_ROLE_PROC_TO_PAY');
        const mainPoCheckbox = document.getElementById('accessControlList14'); // New checkbox
        const viewPoCheckbox = document.getElementById('accessControlList15'); // New checkbox
        const acceptPoCheckbox = document.getElementById('accessControlList16'); // New checkbox
        const mainDoCheckbox = document.getElementById('accessControlList17'); // New checkbox
        const viewDoCheckbox = document.getElementById('accessControlList18'); // New checkbox
        const createDoCheckbox = document.getElementById('accessControlList19'); // New checkbox
        const mainGrCheckbox = document.getElementById('accessControlList20'); // New checkbox
        const viewGrCheckbox = document.getElementById('accessControlList21'); // New checkbox
        const mainInvCheckbox = document.getElementById('accessControlList22'); // New checkbox
        const viewInvCheckbox = document.getElementById('accessControlList23'); // New checkbox
        const createInvCheckbox = document.getElementById('accessControlList24'); // New checkbox
        const mainPymtCheckbox = document.getElementById('accessControlList25'); // New checkbox
        const viewPymtCheckbox = document.getElementById('accessControlList26'); // New checkbox
        var checkboxIds = ['#accessControlList14', '#accessControlList15', '#accessControlList17', '#accessControlList18',
        '#accessControlList20','#accessControlList21', '#accessControlList22', '#accessControlList23', '#accessControlList25',
         '#accessControlList26'];

        // Logic for viewPoListCheckbox (accessControlList14)
        if (!mainPoCheckbox.checked || !viewPoCheckbox.checked) {
            console.log("testing when untick accessControlList14 ");
            checkboxIds = checkboxIds.filter(id => id !== '#accessControlList14'&& id !== '#accessControlList15');
        }
        // Logic for viewDoListCheckbox (accessControlList86)
        if (!mainDoCheckbox.checked || !viewDoCheckbox.checked) {
            console.log("testing when untick accessControlList17");
            checkboxIds = checkboxIds.filter(id => id !== '#accessControlList17'&& id !== '#accessControlList18');
        }

        // Logic for mainGrCheckbox (accessControlList89)
        if (!mainGrCheckbox.checked || !viewGrCheckbox.checked) {
            console.log("testing when untick accessControlList18");
            checkboxIds = checkboxIds.filter(id => id !== '#accessControlList20'&& id !== '#accessControlList21');
        }
        // Logic for mainInvCheckbox (accessControlList92)
        if (!mainInvCheckbox.checked || !viewInvCheckbox.checked) {
            console.log("testing when untick accessControlList20");
            checkboxIds = checkboxIds.filter(id => id !== '#accessControlList22'&& id !== '#accessControlList23');
        }

        // Logic for mainPymtCheckbox (accessControlList97)
        if (!mainPymtCheckbox.checked || !viewPymtCheckbox.checked) {
            console.log("testing when untick accessControlList25");
            checkboxIds = checkboxIds.filter(id => id !== '#accessControlList25'&& id !== '#accessControlList26');
        }

        // After filtering, check if the array is empty and uncheck procureToPayCheckbox if needed
        console.log("checkboxIds.length>>>>>> "+checkboxIds.length);
        if (checkboxIds.length === 0) {
            console.log("No more checkboxes to check, unchecking Procure-to-Pay");
            procureToPayCheckbox.checked = false; // Uncheck Procure-to-Pay
        }
        //console.log("hello array>>>>>> "+checkboxIds);  // Check the modified array
    }
    document.addEventListener("DOMContentLoaded", function() {
        // For supplier role

        if (userHasSupplierRole) {
        console.log("is this even working??");
      document.getElementById('accessControlList14').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList15').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList17').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList18').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList20').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList21').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList22').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList23').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList25').addEventListener('change', toggleUncheckP2PSupplier);
            document.getElementById('accessControlList26').addEventListener('change', toggleUncheckP2PSupplier);
        }

        // For buyer role
        if (userHasBuyerRole) {
        console.log("buyer??");
        document.getElementById('accessControlList80').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList81').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList83').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList84').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList86').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList87').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList89').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList90').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList92').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList93').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList95').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList96').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList97').addEventListener('change', toggleUncheckP2P);
        document.getElementById('accessControlList98').addEventListener('change', toggleUncheckP2P);
        }
    });





