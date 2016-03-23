var cssnum = document.styleSheets.length;
var injected = false;
var ti = setInterval(function() {
	if (document.styleSheets.length > cssnum && injected == false) {
	  Android.finishedInjection();
	  clearInterval(ti);
	  injected = true;
	}
  }, 500);

$(".content").each(function(){
	$(this).find("img").each(function() {
		$(this).on("click", function(e) {
			e.preventDefault();
			Android.showImage($(this).attr("src"));
		});
	});


});


var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
var target = document.querySelector('body');

var observer = new MutationObserver(function(mutations) {
    mutations.forEach(function(mutation) {
        if(mutation.tagName = "DIV") {
            if($(mutation.addedNodes[0]).find('img.PopupImage').length != 0) {
                $(mutation.addedNodes[0]).remove();
            }
        }

    });
});

var config = {
    attributes: true,
    childList: true,
    characterData: true
};


observer.observe(target, config);

if ( $( "#navbar-login>a" ).length ) {
    console.log("USER IS LOGGED IN!");
    var username = $( "#navbar-login>a" ).text();
    var userid = $( "#navbar-login>a" ).attr("href").replace("member.php?u=", "");
    console.log(userid);
    Android.setLoginStatus(true, username, userid);
}
else {
    Android.setLoginStatus(false, "", 0);
}
