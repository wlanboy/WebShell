document.addEventListener('DOMContentLoaded', function () {
  if (window.location.search.includes('error')) {
    document.getElementById('error-box').style.display = '';
  }
});
