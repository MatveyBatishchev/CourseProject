
$(document).ready(function () {
    let searchButton = $("#searchButton");
    let searchInput = $("#searchInput");
    let totalPagesDiv = $("#totalPagesDiv");
    let pageNumberDiv = $("#pageNumberDiv");
    let pagination = $("#pagination");
    let entityTable = $("#entityTable");
    let notFoundMessage = $("#notFoundMessage");

    definePagination(totalPages, false);

    searchButton.on('click', function () {
        if (searchInput.val() === "") {
            definePagination(totalPages, false)
            getEntities(1);
        } else {
            $.ajax({
                type: "GET",
                url: "/" + entity + "/by-search",
                data: {
                    search: searchInput.val(),
                    pageNumber: 0
                },
                success: function (data) {
                    let jsonObject = JSON.parse(data);
                    let entities = JSON.parse(jsonObject.entities);
                    let totalPages = jsonObject.totalPages;
                    definePagination(totalPages, true);
                    insertEntities(entities);
                }
            });
        }
    });

    function definePagination(totalPages, searchMarker) {
        totalPagesDiv.text(totalPages);
        let dataSource = [];
        for (let i = 1; i <= totalPages; i++) {
            dataSource.push(i);
        }
        pageNumberDiv.text(1);

        pagination.pagination({
            dataSource: dataSource,
            pageSize: 1,
            pageRange: 1,
            autoHidePrevious: true,
            autoHideNext: true,
            nextText: 'Следующая',
            prevText: 'Предыдущая',
            hideWhenLessThanOnePage: true,
            afterPageOnClick: function () {
                if (!searchMarker) getEntities(pagination.pagination('getSelectedPageNum'));
                else getEntitiesWithSearch();
            },
            afterNextOnClick: function () {
                if (!searchMarker) getEntities(pagination.pagination('getSelectedPageNum'));
                else getEntitiesWithSearch();
            },
            afterPreviousOnClick: function () {
                if (!searchMarker) getEntities(pagination.pagination('getSelectedPageNum'));
                else getEntitiesWithSearch();
            }
        });
    }

    function getEntities(pageNumber) {
        $.ajax({
            type: "GET",
            url: "/" + entity + "/all/page/" + (pageNumber - 1),
            success: function (data) {
                let entities = JSON.parse(data);
                insertEntities(entities);
            }
        })
        pageNumberDiv.text(pageNumber);
    }

    function getEntitiesWithSearch() {
        let pageNumber = pagination.pagination('getSelectedPageNum');
        $.ajax({
            type: "GET",
            url: "/" + entity + "/by-search",
            data: {
                search: searchInput.val(),
                pageNumber: pageNumber - 1
            },
            success: function (data) {
                let jsonObject = JSON.parse(data);
                let entities = JSON.parse(jsonObject.entities);
                insertEntities(entities);
            }
        });
        pageNumberDiv.text(pageNumber);
    }

    function insertEntities(entities) {
        if (entities.length === 0) {
            pageNumberDiv.text(0);
            notFoundMessage.prop("hidden", false);
        } else {
            notFoundMessage.prop("hidden", true);
        }
        entityTable.html(getInsertedHtml(entities));
    }

});