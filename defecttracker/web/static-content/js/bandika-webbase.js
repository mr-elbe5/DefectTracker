const MODAL_DLG_JQID = '#modalDialog';

function openModalDialog(ajaxCall) {
    $(MODAL_DLG_JQID).load(ajaxCall, function () {
        $(MODAL_DLG_JQID).modal({show: true});
    });
    return false;
}

function closeModalDialog() {
    let $dlg = $(MODAL_DLG_JQID);
    $dlg.html('');
    $dlg.modal('hide');
    $('.modal-backdrop').remove();
    return false;
}

function postByAjax(url, params, identifier) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $(identifier).html(html);
    });
    return false;
}

function postMultiByAjax(url, params, target) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', enctype: 'multipart/form-data', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $(target).html(html);
    });
    return false;
}

function linkTo(url) {
    window.location.href = url;
    return false;
}

$.fn.extend({
    serializeFiles: function () {
        let formData = new FormData();
        $.each($(this).find("input[type='file']"), function (i, tag) {
            $.each($(tag)[0].files, function (i, file) {
                formData.append(tag.name, file);
            });
        });
        let params = $(this).serializeArray();
        $.each(params, function (i, val) {
            formData.append(val.name, val.value);
        });
        return formData;
    }
});

/* text editor */

function initAce(textarea) {
    let mode = textarea.data('editor');
    let editDiv = $('<div>', {
        'class': textarea.attr('class'),
        'style': textarea.attr('style')
    }).insertBefore(textarea);
    textarea.css('display', 'none');
    let editor = ace.edit(editDiv[0]);
    editor.renderer.setShowGutter(textarea.data('gutter'));
    editor.getSession().setValue(textarea.val());
    ace.config.set('basePath', '/js');
    editor.getSession().setMode("ace/mode/" + mode);
    editor.setTheme("ace/theme/crimson_editor");
    return editor;
}

/*
    FlexTable: div with structure

    <div class="flexTable">
        <div class="tableHead">
            <div class="tableRow">
                <div style="flex: x">...</div>
                ...
            </div>
        </div>
        <div class="tableBody">
            <div class="tableRow">
                <div>...</div>
                ...
            </div>
            ...
    </div>

    and styles (scss)

*/


class FlexTable {
    constructor($div, options) {
        /* jquery object of div with structure
            <div class="flexTable">
                <div class="tableHead">
                    <div class="tableRow">
                        <div style="flex: x">...</div>
                        ...
                    </div>
                </div>
                <div class="tableBody">
                    <div class="tableRow">
                        <div>...</div>
                        ...
                    </div>
                    ...
            </div> */
        this.$table = $div;
        // options array as below
        this.options = options || {};
        // result of these
        this.scrollable = this.options.tableHeight || this.options.$container;
    }

    init() {
        let $topHeaders = $('div.tableHead > div.tableRow', this.$table).children();
        let columnCount = $topHeaders.size();
        let colStyles = [];
        for (let i = 0; i < columnCount; i++) {
            let $col = $($topHeaders[i]);
            colStyles[i] = $col.attr('style');
        }
        $('div.tableBody > div.tableRow', this.$table).each(function (index, row) {
            let $cells = $(row).children();
            for (let i = 0; i < columnCount; i++) {
                $($cells[i]).attr('style', colStyles[i]);
            }
        });
        this.resize();
    }

    resize() {
        if (this.scrollable) {
            if (this.options.tableHeight){
                let headerHeight = $('div.tableHead', this.$table).height();
                this.$table.css('max-height', this.options.tableHeight);
                $('div.tableBody', this.$table).css('max-height', (this.$table.height()-headerHeight) + 'px');
            }
            else if (this.options.$container){
                let topDelta = this.$table.offset().top - this.options.$container.offset().top;
                let containerHeight = this.options.$container.height();
                let tableHeight = containerHeight - topDelta - 16;
                this.$table.css('max-height', tableHeight + 'px');
                let headerHeight = $('div.tableHead', this.$table).height();
                let bodyHeight = tableHeight - headerHeight;
                $('div.tableBody', this.$table).css('max-height', bodyHeight + 'px');
            }
            let $firstRow=$('div.tableBody > div.tableRow:first-child', this.$table);
            if ($firstRow) {
                $('div.tableHead > div.tableRow', this.$table).css('width',$firstRow.width() + 'px');
            }
        }

    }
}








