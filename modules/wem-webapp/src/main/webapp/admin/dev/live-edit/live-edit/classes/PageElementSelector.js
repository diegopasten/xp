AdminLiveEdit.PageElementSelector = function()
{
    var selected = null;

    function init()
    {
        initMouseClickEvent();
    }


    function selectElement( element )
    {
        var canvas = AdminLiveEdit.PageCanvas;
        var util = AdminLiveEdit.Util;
        var highlighter = AdminLiveEdit.Highlighter;
        var tooltip = AdminLiveEdit.Tooltip;
        var pageElementTypeToSelect = util.getPageElementType( element );
        var canvasColor, elementBoxModel;

        if ( pageElementTypeToSelect === 'window' ) {
            canvasColor = [0, 0, 0, 0.4];
            elementBoxModel = util.getBoxModelSize( element );
            highlighter.highlightWindow( element, true );
        } else if ( pageElementTypeToSelect === 'region' ) {
            canvasColor = [0, 0, 255, 0.4];
            elementBoxModel = util.getBoxModelSize( element, true );
            highlighter.highlightRegion( element, true );
        }

        canvas.show();
        canvas.resizeCanvas();
        canvas.fillCanvas( canvasColor );
        canvas.clearRectangle( elementBoxModel.left, elementBoxModel.top, elementBoxModel.width, elementBoxModel.height );


        setSelected( element );
        tooltip.moveToPageElement( element );
    }


    function setSelected(element)
    {
        selected = element;
    }


    function getSelected()
    {
        return selected;
    }


    function initMouseClickEvent()
    {
        $liveedit('body').on('click', '[data-live-edit-region], [data-live-edit-window]',  function(event) {
            event.stopPropagation();
            selectElement( $liveedit( this ) );
        });
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        selectElement: function(element) {
            selectElement(element);
        },

        setSelected: function(element) {
            setSelected(element);
        },

        getSelected: function() {
            getSelected();
        }
    };

}();