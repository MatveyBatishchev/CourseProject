
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
        let tbodies = "";
        if (entity === "patients") {
            tbodies += "<table class=\"table table-striped table-hover mt-5\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <th scope=\"col\">ID</th>\n" +
                "                    <th scope=\"col\">Имя</th>\n" +
                "                    <th scope=\"col\">Фамилия</th>\n" +
                "                    <th scope=\"col\">Дата рождения</th>\n" +
                "                    <th scope=\"col\">Пол</th>\n" +
                "                    <th scope=\"col\">E-mail</th>\n" +
                "                    <th scope=\"col\">Телефон</th>\n" +
                "                </tr>\n" +
                "                </thead>"
            entities.forEach(entity => {
                tbodies += '<tbody>\n' +
                    '                <tr>\n' +
                    '                    <th scope="row"><a href="/patients/' + entity.id + '">' + entity.id +
                    '                    </a></th>\n' +
                    '                    <td>' + entity.name + '</td>\n' +
                    '                    <td>' + entity.surname + '</td>\n' +
                    '                    <td>' + entity.birthDate + '</td>\n' +
                    '                    <td>' + entity.sex + '</td>\n' +
                    '                    <td>' + entity.email + '</td>\n' +
                    '                    <td>' + entity.telephone + '</td>\n' +
                    '                </tr>\n' +
                    '                </tbody>\n';
            });
        }
        if (entity === "doctors") {
            tbodies += "<table class=\"table table-striped table-hover mt-5\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <th scope=\"col\">ID</th>\n" +
                "                    <th scope=\"col\">Фамилия</th>\n" +
                "                    <th scope=\"col\">Имя</th>\n" +
                "                    <th scope=\"col\">Отчество</th>\n" +
                "                    <th scope=\"col\">Специальность</th>\n" +
                "                    <th scope=\"col\">Кабинет</th>\n" +
                "                    <th scope=\"col\">Стаж</th>\n" +
                "                    <th scope=\"col\">Категория</th>\n" +
                "                </tr>\n" +
                "                </thead>"
            entities.forEach(entity => {
                tbodies += '<tbody>\n' +
                    '                <tr>\n' +
                    '                    <th scope="row"><a href="/doctors/' + entity.id + '">' + entity.id +
                    '                    </a></th>\n' +
                    '                    <td>' + entity.name + '</td>\n' +
                    '                    <td>' + entity.surname + '</td>\n' +
                    '                    <td>' + entity.patronymic + '</td>\n' +
                    '                    <td>' + entity.specialities + '</td>\n' +
                    '                    <td>' + entity.cabinet + '</td>\n' +
                    '                    <td>' + entity.experience + '</td>\n' +
                    '                    <td>' + entity.category + '</td>\n' +
                    '                </tr>\n' +
                    '                </tbody>\n';
            });
        }
        tbodies += '</table>';
        entityTable.html(tbodies);
    }

});