// adapted from bootsnipp
$.fn.extend({
    treed: function (openedClass, closedClass) {

        //initialize each of the top levels
        var tree = $(this);
        tree.addClass("tree");
        tree.find('li').has("ul").each(function () {
            var branch = $(this); //li with children ul
            var open = branch.hasClass('open');
            var currentClass = open ? openedClass : closedClass;
            branch.prepend("<i class='treeIndicator " + currentClass + "'></i>");
            branch.addClass('branch');
            var icon = branch.children('i:first');
            icon.on('click', function (e) {
                if (this === e.target) {
                    $(this).toggleClass(openedClass + " " + closedClass);
                    $(this).parent().children().children('li').toggle();
                }
            });
            if (!open) {
                branch.children().children('li').toggle();
            }
        });
        //fire event from the dynamically added icon
        tree.find('.branch .indicator').each(function () {
            $(this).on('click', function () {
                $(this).closest('li').click();
            });
        });
        //fire event to open branch if the li contains an anchor instead of text
        tree.find('.branch>a').each(function () {
            $(this).on('click', function (e) {
                $(this).closest('li').click();
                e.preventDefault();
            });
        });
        //fire event to open branch if the li contains a button instead of text
        tree.find('.branch>button').each(function () {
            $(this).on('click', function (e) {
                $(this).closest('li').click();
                e.preventDefault();
            });
        });
    }
});
