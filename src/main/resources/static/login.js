document.addEventListener('DOMContentLoaded', function () {
  if (window.location.search.includes('error')) {
    document.getElementById('error-box').style.display = '';
  }

  // Spring Security sets the CSRF token as cookie; a plain form post must send it as field.
  document.getElementById('login-form').addEventListener('submit', function () {
    const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
    document.getElementById('csrf').value = match ? decodeURIComponent(match[1]) : '';
  });
});
