document.getElementById('shell-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const command = document.getElementById('command').value;
  const output  = document.getElementById('output');
  const errorBox = document.getElementById('error-box');
  const errorMsg = document.getElementById('error-msg');

  output.textContent = '';
  errorBox.style.display = 'none';

  const params = new URLSearchParams();
  params.append('command', command);

  try {
    const resp = await fetch('/execute', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    });

    if (resp.status === 401 || resp.status === 403) {
      window.location.href = '/login.html';
      return;
    }

    const text = await resp.text();

    if (!resp.ok) {
      errorBox.style.display = '';
      errorMsg.textContent = text;
      return;
    }

    output.textContent = text;
  } catch (err) {
    errorBox.style.display = '';
    errorMsg.textContent = err.message;
  }
});
