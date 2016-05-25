function CreateFloatingDiv(t,e,n,o){var i=$("#"+n)[0];if(i)return"block"==i.style.display?($(i).hide("fast"),!1):(i.style.left=t+"px",i.style.top=e+"px",i.style.display="none",$(i).show("fast"),!1);var r=document.createElement("div");return r.style.left=t+"px",r.style.top=e+"px",r.className="top popupbox "+o,r.id=n,document.body.appendChild(r),r}function CloseThis(t,e){return $(t).hasClass("top")?(e?(t.innerHTML=e,$(t).fadeOut(3e3)):$(t).hide(),!1):CloseThis(t.parentNode,e)}function OpenEventlog(t){var e=CreateFloatingDiv(MouseX,MouseY,"Event_"+t,"eventlog");return e?(e.innerHTML+="Fetching...",$(e).click(function(){$(e).hide()}),$(e).show("fast"),$.get(window.location.origin+"/fp_events.php","aj=1&user="+t,function(t){e.innerHTML=t},"html"),!1):!1}function OpenURLInBox(t,e){var n=CreateFloatingDiv(MouseX,MouseY,t,"urlbox");return n?(n.innerHTML+="Fetching...",$(n).click(function(){$(n).hide()}),$(n).show("fast"),$.get(window.location.origin+"/"+e,"",function(t){n.innerHTML=t},"html"),!1):!1}function RatePost(t,e,n){return $.post(window.location.origin+"/ajax.php",{"do":"rate_post",postid:t,rating:e,key:n,securitytoken:SECURITYTOKEN},function(e){var n=$("#rating_"+t),o=$("#ratingcontrols_post_"+t),i=void 0!=e.error,r=i?e.error:e.message;i||n.fadeOut("fast",function(){n[0].innerHTML=e.result,n.fadeIn("fast")}),o.css({opacity:"1",color:"#888"}),o[0].innerHTML=r},"json"),$("#ratingslist_"+t).remove(),$("#ratingcontrols_post_"+t)[0].innerHTML="",!1}function ReportPost(t){var e=CreateFloatingDiv(MouseX,MouseY,"Report_"+t,"report");e&&(e.innerHTML="Wait...",$(e).show("fast"),$.get(window.location.origin+"/ajax.php",{"do":"report_text",postid:t},function(t){e.innerHTML=t},"html"))}function SendReportPost(t,e){$.post(window.location.origin+"/ajax.php",{"do":"report_submit",postid:t,reasonid:e,securitytoken:SECURITYTOKEN},function(t){t&&alert(t)},"html")}function InitReportPage(){$(document).ready(function(){$(".post_report .userbox .ignore_link").click(function(){var t=this.id;return $(".userbox_"+t).fadeOut("fast",function(){$(this).siblings().length<2?$(this).parents(".post_report").slideUp("fast",function(){$(this).remove()}):$(this).remove()}),$.post(window.location.origin+"/ajax.php",{"do":"report_ignoreuser",userid:t,securitytoken:SECURITYTOKEN}),!1}),$(".post_report a.dismiss").click(function(){var t=this.id;return $(this).parents(".post_report").slideUp("fast",function(){$(this).remove()}),$.post(window.location.origin+"/ajax.php",{"do":"report_dismissreport",postid:t,securitytoken:SECURITYTOKEN}),!1})})}function InitQueuePage_Upgrades(){$(document).ready(function(){$(".upgradebox .approvebuttons a").click(function(){var t=$(this).attr("action"),e=$(this).parents(".upgradebox"),n=e.attr("queueid");$.post(window.location.origin+"/ajax.php",{"do":"queue_dismiss",provider:"upgrades",itemid:n,action:t,securitytoken:SECURITYTOKEN},function(t){if(e.animate({backgroundColor:"#ccc"},"fast"),void 0!==t.error)var n="<strong>Error:</strong> "+t.error;else var n=t.message;n="<em>"+n+"</em>",e.children(".upgradetext").html(n)},"json"),e.children(".approvebuttons").fadeOut("fast")})})}function OpenPostAdmin(t,e,n,o){var i=MouseX,r=MouseY;return $("#postadmin_"+e).length>0&&$("#postadmin_"+e).remove(),$("img#PAIMG_"+e).attr("src","fp/progress.gif"),$.post(window.location.origin+"/ajax.php",{"do":"postadmin",action:"postadmin_text",type:"user",threadid:t,postid:e,userid:n,verify:o,securitytoken:SECURITYTOKEN},function(t){if(t.getElementsByTagName("response").length>0)return void alert(t.getElementsByTagName("response")[0].firstChild.nodeValue);var n=t.getElementsByTagName("postadmin")[0].attributes.getNamedItem("width").value,o=document.createElement("div");o.id="postadmin_"+e,o.className="postadmin",o.style.left=i-n-20+"px",o.style.top=r+"px",o.style.width=n+"px",o.style.display="none",o.innerHTML=t.getElementsByTagName("postadmin")[0].firstChild.nodeValue,document.body.appendChild(o),$("#PAC_"+e).click(function(){return $(o).slideUp("fast",function(){$(this).remove()}),!1}),$("#PAEL_"+e).click(function(){return $(this).fadeOut("fast"),$("#postadmin_eventlog_"+e).slideToggle("fast",function(){var t=$("#PAEL_"+e)[0];"none"==$(this).css("display")?t.innerHTML="Show":t.innerHTML="Hide",$(t).fadeIn("fast")}),!1}),$(o).slideDown("fast"),$("img#PAIMG_"+e).attr("src","fp/postadmin.png"),$("#PAR_"+e).focus()},"xml"),!1}function AdminUser(t,e,n,o,i){var r=$("#PAR_"+n)[0].value;return $.post(window.location.origin+"/ajax.php",{"do":"postadmin",action:t,type:"user",userid:e,postid:n,threadid:o,reason:r,verify:i,securitytoken:SECURITYTOKEN},function(t){$("#postadmin_response_"+n)[0].innerHTML+=t.getElementsByTagName("response")[0].firstChild.nodeValue+"<br />"},"xml"),"<em>None</em><br>"==$("#postadmin_response_"+n)[0].innerHTML&&($("#postadmin_response_"+n)[0].innerHTML=""),$("#postadmin_response_"+n)[0].innerHTML+='<span style="color:green;">&gt;&gt;'+t+"..</span><br />",!1}function NumOccur(t,e){var n=t.match(new RegExp(e,"g"));return n?2*n.length:0}function WorkOutLanguage(t){var e=new Array,n=0;n+=NumOccur(t,"self:"),n+=NumOccur(t,"\\-\\- "),n+=NumOccur(t,"function ENT\\:"),n+=NumOccur(t,"function GM\\:"),n+=NumOccur(t,"\\) do"),n+=NumOccur(t,"\\) then"),n+=NumOccur(t,"hook\\.Add"),n+=NumOccur(t,"then return"),e.push({name:"lua",score:n});var n=0;n+=NumOccur(t,"using System"),n+=NumOccur(t,"public class "),n+=NumOccur(t,"private class "),n+=NumOccur(t,"public float "),n+=NumOccur(t,"private float "),n+=NumOccur(t,"public bool "),n+=NumOccur(t,"private bool "),n+=NumOccur(t,"Time.deltaTime"),n+=NumOccur(t,"rigidbody.AddForce\\("),n+=NumOccur(t,"Vector3.forward"),n+=NumOccur(t,"Vector3.right"),e.push({name:"csharp",score:n});var n=1;return e.push({name:"c",score:n}),e.sort(function(t,e){return t.score<e.score}),e[0].name}function InitShowthread(){$("#threadadmin_toggle a").click(function(){return $("#threadadmin").stop().slideToggle("fast"),!1}),$("a[rel=tipsy]").tipsy({gravity:"s"}),$("ol#posts > li").each(function(){$("#ratingcontrols_"+this.id).hide(),$(this).hover(function(){$("#ratingcontrols_"+this.id).fadeIn(150)},function(){$("#ratingcontrols_"+this.id).fadeOut(150)})}),$("PRE.bbcode_code").each(function(t){$(this).attr("data-language",WorkOutLanguage($(this).text()))})}function AdminThread(t,e,n,o){return o||(o=$("#threadadmin_reason")[0].value),$.post(window.location.origin+"/ajax.php",{"do":"postadmin",type:"thread",action:t,threadid:e,reason:o,verify:n,securitytoken:SECURITYTOKEN},function(t){$("#threadadmin_response")[0].innerHTML+=t.getElementsByTagName("response")[0].firstChild.nodeValue+"<br />"}),$("#threadadmin_response")[0].innerHTML+='<span style="color:green;">&gt;&gt;'+t+"..</span><br />",!1}function RatingsList(t){return $("#ratingslist_"+t).length>0?($("#ratingslist_"+t).slideToggle("fast"),!1):($.get(window.location.origin+"/ajax.php",{"do":"rate_list",postid:t},function(e){var n=$("#rating_"+t).offset().left,o=$("#rating_"+t).offset().top+$("#rating_"+t).height()+9,i=CreateFloatingDiv(n,o,"ratingslist_"+t,"ratingslist");i.innerHTML=e.list,$(i).click(function(){$(this).slideUp("fast")}),$(i).slideDown("fast")},"json"),!1)}function UnreadThread(t,e){return $(e).html("<img height='9px' src='/images/misc/11x11progress.gif' />"),$.post(window.location.origin+"/ajax.php",{"do":"unreadthread",threadid:t,securitytoken:SECURITYTOKEN},function(n){$(e).remove(),$("#thread_"+t+" .newposts").remove(),-1!=window.location.href.indexOf("fp_read.php")&&($("#thread_"+t+" > td").animate({paddingTop:0,paddingBottom:0},200),$("#thread_"+t+" > td > *").slideUp(200,function(){$("#thread_"+t).remove()}))}),!1}MouseX=0,MouseY=0,$(document).mousemove(function(t){MouseX=t.pageX,MouseY=t.pageY}),window.location.origin||(window.location.origin=window.location.protocol+"//"+window.location.hostname+(window.location.port?":"+window.location.port:"")),$(function(){for(var t=document.getElementsByClassName("prefix understate"),e=document.getElementById("navbar-login").lastChild.previousSibling.text,n=0;n<t.length;n++){var o=t[n].children[0];if(o){var i=o.text||o.innerText||o.textContent||o.innerHTML;i==e&&(t[n].style.backgroundColor="rgb(255, 133, 0)")}}});

/*

Original JS end

*/

/*
var cssnum = document.styleSheets.length;
var injected = false;
var ti = setInterval(function() {
	if (document.styleSheets.length > cssnum && injected == false) {
	  Android.finishedInjection();
	  clearInterval(ti);
	  injected = true;
	}
  }, 500);

*/



jQuery(function() {
    console.log("DOM READY");
    $(".content").each(function(){
        $(this).find("img").each(function() {
            $(this).click(function(e) {
                e.preventDefault();
                Android.showImage($(this).attr("src"));
            });
        });
    });

   var getUrlParameter = function getUrlParameter(sParam, url) {

       var sPageURL = decodeURIComponent(window.location.search.substring(1)),
           sURLVariables = sPageURL.split('&'),
           sParameterName,
           i;


       for (i = 0; i < sURLVariables.length; i++) {
           sParameterName = sURLVariables[i].split('=');

           if (sParameterName[0] === sParam) {
               return sParameterName[1] === undefined ? true : sParameterName[1];
           }
       }
   };

   function getQueryVariable(variable, url)
   {
          var query = url;
          var vars = query.split("&");
          for (var i=0;i<vars.length;i++) {
                  var pair = vars[i].split("=");
                  if(pair[0] == variable){return pair[1];}
          }
          return(false);
   }



   if (window.location.href.indexOf("showthread.php") > -1 || window.location.href.indexOf("forumdisplay.php") > -1 || window.location.href.indexOf("search.php") > -1 ) {
       var pagination = null;

       if ( $(".pagination").length ) {
            pagination = $(".pagination");
       }

       if ( $("#pagination_top").length ) {
             pagination = $("#pagination_top");
       }


       if ( $(".pagination").length || $("#pagination_top").length ) {

            console.log("Show pagination!");

            console.log("found it");
            var currentPage = 1;
            /* Get current page*/
            if(getUrlParameter('page') == null) {
                console.log(pagination.first().find('.selected a').text());
                currentPage = pagination.first().find('.selected a').text();
            }
            else
            {
               console.log(getUrlParameter('page'));
               currentPage = getUrlParameter('page');
            }

            /* Get link url */
            var lastpage = pagination.find('span').last().find('a').attr("href");
            console.log(lastpage);
            if(lastpage.indexOf("javascript://") > -1) {
               lastpage = pagination.find('span').last().find('a').text();
               console.log("Is on last page");
            } else {
               lastpage = getQueryVariable("page", lastpage);
               console.log(lastpage);
            }

            Android.setupPagination(currentPage, lastpage);
            Android.showPagination();

            /* Detect scrolled at bottom */
            $(window).scroll(function() {
               if($(window).scrollTop() + $(window).height() == $(document).height()) {
                   Android.showPagination();
               }
            });
       } else {
            console.log("Hidepagination!");
            $(window).unbind('scroll');
            Android.disablePagination();
            Android.hidePagination();
       }
   } else {
        console.log("Hidepagination!");
        $(window).unbind('scroll');
        Android.disablePagination();
        Android.hidePagination();
   }


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
        $( "#navbar-login" ).hide();
        var username = $( "#navbar-login>a" ).text();
        var userid = $( "#navbar-login>a" ).attr("href").replace("member.php?u=", "");
        console.log(userid);
        Android.setLoginStatus(true, username, userid);
    }
    else {
        Android.setLoginStatus(false, "", 0);
    }


    // Video click handling
    $("video").each(function () {
        var thisSrc = $(this).attr("src");
        $(this).wrap("<div class='videoContainer'></div>");
        $(this).after( '<a class="videoOptions" style="display: block;" data-url="'+ thisSrc +'">Click for options</a>' );
    });

    $(".videoOptions").click(function(){
        var videoURL = $(this).prev().attr("src");
        console.log(videoURL);
        Android.openVideoContextMenu(videoURL);
        return false;
    });


});

function insertAtCaret(text) {
        var txtarea = document.activeElement;
        var scrollPos = txtarea.scrollTop;
        var strPos = 0;
        var br = ((txtarea.selectionStart || txtarea.selectionStart == '0') ?
            "ff" : (document.selection ? "ie" : false ) );
        if (br == "ie") {
            txtarea.focus();
            var range = document.selection.createRange();
            range.moveStart ('character', -txtarea.value.length);
            strPos = range.text.length;
        }
        else if (br == "ff") strPos = txtarea.selectionStart;

        var front = (txtarea.value).substring(0,strPos);
        var back = (txtarea.value).substring(strPos,txtarea.value.length);
        txtarea.value=front+text+back;
        strPos = strPos + text.length;
        if (br == "ie") {
            txtarea.focus();
            var range = document.selection.createRange();
            range.moveStart ('character', -txtarea.value.length);
            range.moveStart ('character', strPos);
            range.moveEnd ('character', 0);
            range.select();
        }
        else if (br == "ff") {
            txtarea.selectionStart = strPos;
            txtarea.selectionEnd = strPos;
            txtarea.focus();
        }
        txtarea.scrollTop = scrollPos;
    }