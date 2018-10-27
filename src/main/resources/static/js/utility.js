function showBucketList(id) {
    location.href = "/bucketlist/details/" + id;
}

/*function showBucketListWithoutEditingOptions(id) {
    location.href = "/bucketlist/details/" + id;
}*/

function addItem(){
    location.href = "/bucketlists/addnew"
}

function toggleItemTagged(id, isAuthorized) {
    if(isAuthorized){
        toggleItemMark(id);
        console.log("inside toggleItemTagged func");
    }
}

function toggleItemMark(id){
    if(document.getElementById("itemTagMarkIcon" + id).getAttribute("data-tagged") == "false"){
        markItem(id);
        console.log("inside toggleItemMark funcm mark branch");
    }else{
        unmarkItem(id);
        console.log("inside toggleItemMark funcm unmark branch");
    }
}

function showItemMark(id){
    document.getElementById("itemTagMarkIcon" + id).style.backgroundImage = "url('/img/mark_filled.svg')";
    var curCount = parseInt(document.getElementById("itemCounter" + id).textContent);
    curCount += 1;
    document.getElementById("itemCounter" + id).innerText = curCount;
    document.getElementById("itemTagMarkIcon" + id).setAttribute("data-tagged", "true");
}

function showUnmarkItem(id) {
    document.getElementById("itemTagMarkIcon" + id).style.backgroundImage = "url('/img/mark.svg')";
    var curCount = parseInt(document.getElementById("itemCounter" + id).textContent);
    curCount -= 1;
    document.getElementById("itemCounter" + id).innerText = curCount;
    document.getElementById("itemTagMarkIcon" + id).setAttribute("data-tagged", "false");
}

function markItem(id) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200){
            console.log("operation ok " + id);
            showItemMark(id);
        }else if(this.status == 401){
            alert("You can't do that here ! You're not authorized to do so.")
        }
    };
    xhttp.open("POST", "/markitem/"+id);
    xhttp.send();
}

function unmarkItem(id){
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200){
            console.log("operation unmark ok " + id);
            showUnmarkItem(id);
        }else if(this.status == 401){
            alert("You can't do that here ! You're not authorized to do so.")
        }
    };
    xhttp.open("POST", "/unmarkitem/"+id);
    xhttp.send();
}

function loadFile(event){
        var output = document.getElementById('imageframe');
        output.src = URL.createObjectURL(event.target.files[0]);
}