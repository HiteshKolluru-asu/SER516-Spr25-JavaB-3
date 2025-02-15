function showResult(resultId) {
    document.getElementById(resultId).style.display = 'block';
}


document.getElementById('submitAfferent').addEventListener('click', function() {
    showResult('resultAfferent');
});
