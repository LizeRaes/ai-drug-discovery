document.addEventListener("DOMContentLoaded", function () {

    //const dropdown = document.getElementById("restoreList");
    //let selectedValue = "";

    //dropdown.addEventListener("change", function () {
      //  selectedValue = dropdown.value;
    //});

    document.getElementById("restoreButton").addEventListener("click", function () {
        const messageArea = document.getElementById('messageArea');
        messageArea.innerHTML = "";

        const selectedValue = document.getElementById("restoreList").value;

        // Perform the REST call
        fetch(`http://localhost:8080/history/load/${selectedValue}`, {
            method: "POST"
        })
            .then(response => {
                if (response.ok) {
                    alert("History loaded !")
                } else {
                    alert("History not loaded !")
                }
            })
            .catch(error => {
                alert("An error occurs during loading the history !")
            });
    });

    document.getElementById("saveButton").addEventListener("click", function () {
        const saveInput = document.getElementById('saveInput').value;

        // Perform the REST call
        fetch(`http://localhost:8080/history/save/${saveInput}`, {
            method: "POST"
        })
            .then(response => {
                if (response.ok) {
                    alert("History saved !")
                    document.getElementById('saveInput').value = "";
                } else {
                    alert("History not saved !")
                }
            })
            .catch(error => {
                alert("An error occurs during saving the history !")
            });
    });
});