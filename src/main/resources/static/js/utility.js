function showBucketList(id) {
    location.href = "/bucketlist/details/" + id;
}

/*function showBucketListWithoutEditingOptions(id) {
    location.href = "/bucketlist/details/" + id;
}*/

function addItem(){
    location.href = "/bucketlists/addnew"
}

function toggleItemTagged(id) {

    if(document.getElementById("itemTagMarkIcon" + id).getAttribute("data-tagged") == "false"){
        document.getElementById("itemTagMarkIcon" + id).style.backgroundImage = "url('/img/mark_filled.svg')";
        var curCount = parseInt(document.getElementById("itemCounter" + id).textContent);
        curCount += 1;
        document.getElementById("itemCounter" + id).innerText = curCount;
        document.getElementById("itemTagMarkIcon" + id).setAttribute("data-tagged", "true");
    }else{
        document.getElementById("itemTagMarkIcon" + id).style.backgroundImage = "url('/img/mark.svg')";
        var curCount = parseInt(document.getElementById("itemCounter" + id).textContent);
        curCount -= 1;
        document.getElementById("itemCounter" + id).innerText = curCount;
        document.getElementById("itemTagMarkIcon" + id).setAttribute("data-tagged", "false");
    }

}