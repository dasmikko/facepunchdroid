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

/*!
 * in-view 0.6.1 - Get notified when a DOM element enters or exits the viewport.
 * Copyright (c) 2016 Cam Wiegert <cam@camwiegert.com> - https://camwiegert.github.io/in-view
 * License: MIT
 */
!function(t,e){"object"==typeof exports&&"object"==typeof module?module.exports=e():"function"==typeof define&&define.amd?define([],e):"object"==typeof exports?exports.inView=e():t.inView=e()}(this,function(){return function(t){function e(r){if(n[r])return n[r].exports;var i=n[r]={exports:{},id:r,loaded:!1};return t[r].call(i.exports,i,i.exports,e),i.loaded=!0,i.exports}var n={};return e.m=t,e.c=n,e.p="",e(0)}([function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}var i=n(2),o=r(i);t.exports=o["default"]},function(t,e){function n(t){var e=typeof t;return null!=t&&("object"==e||"function"==e)}t.exports=n},function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}Object.defineProperty(e,"__esModule",{value:!0});var i=n(9),o=r(i),u=n(3),f=r(u),s=n(4),c=function(){if("undefined"!=typeof window){var t=100,e=["scroll","resize","load"],n={history:[]},r={offset:{},threshold:0,test:s.inViewport},i=(0,o["default"])(function(){n.history.forEach(function(t){n[t].check()})},t);e.forEach(function(t){return addEventListener(t,i)}),window.MutationObserver&&addEventListener("DOMContentLoaded",function(){new MutationObserver(i).observe(document.body,{attributes:!0,childList:!0,subtree:!0})});var u=function(t){if("string"==typeof t){var e=[].slice.call(document.querySelectorAll(t));return n.history.indexOf(t)>-1?n[t].elements=e:(n[t]=(0,f["default"])(e,r),n.history.push(t)),n[t]}};return u.offset=function(t){if(void 0===t)return r.offset;var e=function(t){return"number"==typeof t};return["top","right","bottom","left"].forEach(e(t)?function(e){return r.offset[e]=t}:function(n){return e(t[n])?r.offset[n]=t[n]:null}),r.offset},u.threshold=function(t){return"number"==typeof t&&t>=0&&t<=1?r.threshold=t:r.threshold},u.test=function(t){return"function"==typeof t?r.test=t:r.test},u.is=function(t){return r.test(t,r)},u.offset(0),u}};e["default"]=c()},function(t,e){"use strict";function n(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}Object.defineProperty(e,"__esModule",{value:!0});var r=function(){function t(t,e){for(var n=0;n<e.length;n++){var r=e[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(t,r.key,r)}}return function(e,n,r){return n&&t(e.prototype,n),r&&t(e,r),e}}(),i=function(){function t(e,r){n(this,t),this.options=r,this.elements=e,this.current=[],this.handlers={enter:[],exit:[]},this.singles={enter:[],exit:[]}}return r(t,[{key:"check",value:function(){var t=this;return this.elements.forEach(function(e){var n=t.options.test(e,t.options),r=t.current.indexOf(e),i=r>-1,o=n&&!i,u=!n&&i;o&&(t.current.push(e),t.emit("enter",e)),u&&(t.current.splice(r,1),t.emit("exit",e))}),this}},{key:"on",value:function(t,e){return this.handlers[t].push(e),this}},{key:"once",value:function(t,e){return this.singles[t].unshift(e),this}},{key:"emit",value:function(t,e){for(;this.singles[t].length;)this.singles[t].pop()(e);for(var n=this.handlers[t].length;--n>-1;)this.handlers[t][n](e);return this}}]),t}();e["default"]=function(t,e){return new i(t,e)}},function(t,e){"use strict";function n(t,e){var n=t.getBoundingClientRect(),r=n.top,i=n.right,o=n.bottom,u=n.left,f=n.width,s=n.height,c={t:o,r:window.innerWidth-u,b:window.innerHeight-r,l:i},a={x:e.threshold*f,y:e.threshold*s};return c.t>e.offset.top+a.y&&c.r>e.offset.right+a.x&&c.b>e.offset.bottom+a.y&&c.l>e.offset.left+a.x}Object.defineProperty(e,"__esModule",{value:!0}),e.inViewport=n},function(t,e){(function(e){var n="object"==typeof e&&e&&e.Object===Object&&e;t.exports=n}).call(e,function(){return this}())},function(t,e,n){var r=n(5),i="object"==typeof self&&self&&self.Object===Object&&self,o=r||i||Function("return this")();t.exports=o},function(t,e,n){function r(t,e,n){function r(e){var n=x,r=m;return x=m=void 0,E=e,w=t.apply(r,n)}function a(t){return E=t,j=setTimeout(h,e),M?r(t):w}function l(t){var n=t-O,r=t-E,i=e-n;return _?c(i,g-r):i}function d(t){var n=t-O,r=t-E;return void 0===O||n>=e||n<0||_&&r>=g}function h(){var t=o();return d(t)?p(t):void(j=setTimeout(h,l(t)))}function p(t){return j=void 0,T&&x?r(t):(x=m=void 0,w)}function v(){void 0!==j&&clearTimeout(j),E=0,x=O=m=j=void 0}function y(){return void 0===j?w:p(o())}function b(){var t=o(),n=d(t);if(x=arguments,m=this,O=t,n){if(void 0===j)return a(O);if(_)return j=setTimeout(h,e),r(O)}return void 0===j&&(j=setTimeout(h,e)),w}var x,m,g,w,j,O,E=0,M=!1,_=!1,T=!0;if("function"!=typeof t)throw new TypeError(f);return e=u(e)||0,i(n)&&(M=!!n.leading,_="maxWait"in n,g=_?s(u(n.maxWait)||0,e):g,T="trailing"in n?!!n.trailing:T),b.cancel=v,b.flush=y,b}var i=n(1),o=n(8),u=n(10),f="Expected a function",s=Math.max,c=Math.min;t.exports=r},function(t,e,n){var r=n(6),i=function(){return r.Date.now()};t.exports=i},function(t,e,n){function r(t,e,n){var r=!0,f=!0;if("function"!=typeof t)throw new TypeError(u);return o(n)&&(r="leading"in n?!!n.leading:r,f="trailing"in n?!!n.trailing:f),i(t,e,{leading:r,maxWait:e,trailing:f})}var i=n(7),o=n(1),u="Expected a function";t.exports=r},function(t,e){function n(t){return t}t.exports=n}])});



jQuery(function() {

    // Show and hide ratings if they are in the viewport!
    inView('.postfoot')
      .on('enter', el => {
        jQuery(el).find('.postrating').fadeIn('fast');
      })
      .on('exit', el => {
        jQuery(el).find('.postrating').fadeOut('fast');
    });


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
        Android.setLoginStatus(true, username, userid, SECURITYTOKEN);
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


    // Mark unread confirm handling
    /*console.log($(".author span a").attr("onclick")):
    $(".author span a").on('click', function (e) {
        $(this).data('onclick', this.onclick);

        this.onclick = function(event) {
            var thisConfirm = confirm("Are you sure?");
            if(!thisConfirm) { // HERE
                return false;
            } else {
                $(this).data('onclick').call(this, event || window.event);
            };


        };
    });

    $('.author span a').each(function() {
        var handler = $(this).prop('onclick');
        $(this).removeProp('onclick');
        $(this).click(handler);
    });*/


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