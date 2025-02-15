function showResult(resultId) {
    document.getElementById(resultId).style.display = 'block';
}

// Afferent Coupling Button Click
document.getElementById('submitAfferent').addEventListener('click', function() {
    showResult('resultAfferent');
});

// Efferent Coupling Button Click
document.getElementById('submitEfferent').addEventListener('click', function() {
    showResult('resultEfferent');
});

// Defect Density Button Click
document.getElementById('submitDefect').addEventListener('click', function() {
    showResult('resultDefect');
});
