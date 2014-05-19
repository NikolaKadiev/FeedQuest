$(document).ready(function () {
    $("#urlInputAreaSubmitButton").click(function () {
        var urlInputValue = $("#inputUrl").val();
        if (urlInputValue != "" && checkUrl(urlInputValue) != null) {
            var text = $("#inputUrl").val();
            $.ajax({
                url: "/find",
                type: "get",
                data: {
                    searchText: text
                },
                success: function (jsonResponse) {
                    $("#feedList").empty();
                    var $ul = $('<ul>').appendTo($('#feedList'));
                    $.each(jsonResponse, function (index, item) {
                        $('<li>').text(item).appendTo($ul);
                    });
                    $("#searchFeedsButton").css("display", "block");
                },
                error: function (xhr) {
                    alert("Error");
                }

            });
        } else {
            alert("Field can't be empty and must contain a valid url");
        }
    });

    $("#searchFeedsButton").click(function () {
        $("#searchResults").empty();
        var $urlList = $('#feedList li');
        $urlList.each(function () {
            $.ajax({
                url: "/processContent",
                type: "get",
                data: {
                    'url': $(this).text()
                },
                success: function (jsonResponse) {

                    var $ul = $('<ul>').appendTo($('#searchResults'));

                    $.each(jsonResponse, function (i, val) {
                        console.log(i, val);
                        var $li = $('<li>').text(val.url).appendTo($ul);
                        var urlLink = $("<a>").appendTo($li);
                        urlLink.text("Open link");
                        urlLink.attr("href", val.url);
                        urlLink.attr("target", "_blank");
                        var matchngKeywordsCount = $("<p>").appendTo($li);
                        matchngKeywordsCount.text("Matching keywords: " + val.count);
                    });




                },
                error: function () {
                    alert("Error while getting data from feed url");
                }
            });
        });



    });

    function checkUrl(url) {
        return url.match(/(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/);
    }

});