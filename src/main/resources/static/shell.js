const output   = document.getElementById('output');
const input    = document.getElementById('cmd-input');
const terminal = document.getElementById('terminal');

const history = [];
let histIdx = -1;

function appendCmd(cmd) {
  const el = document.createElement('div');
  el.className = 'entry-cmd';
  el.textContent = 'user@webshell:~$ ' + cmd;
  output.appendChild(el);
  output.scrollTop = output.scrollHeight;
}

function appendOut(text, isError) {
  if (!text) return;
  const el = document.createElement('pre');
  el.className = 'entry-out' + (isError ? ' error' : '');
  el.textContent = text;
  output.appendChild(el);
  output.scrollTop = output.scrollHeight;
}

function csrfToken() {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
  return match ? decodeURIComponent(match[1]) : '';
}

async function helpText() {
  let settings = 'Settings could not be loaded.';
  try {
    const resp = await fetch('/settings');
    if (resp.ok) {
      const s = await resp.json();
      settings = `Working directory: ${s.workdir}  |  Timeout: ${s.timeoutSeconds}s  |  Max output: ${s.maxOutputBytes} bytes`;
    }
  } catch (err) {
    // keep fallback text
  }
  return `Built-in commands:
  clear    clear the terminal
  help     show this help

All other commands are executed on the server, without a shell:
no pipes, redirects, variables or globs. Quote arguments with
'single' or "double" quotes, escape single characters with \\.
${settings}`;
}

appendOut('WebShell ready. Type \'help\' for built-in commands.\n');

input.addEventListener('keydown', async function (e) {
  if (e.key === 'ArrowUp') {
    e.preventDefault();
    if (histIdx < history.length - 1) histIdx++;
    input.value = history[history.length - 1 - histIdx] ?? '';
    requestAnimationFrame(() => input.setSelectionRange(input.value.length, input.value.length));
    return;
  }
  if (e.key === 'ArrowDown') {
    e.preventDefault();
    if (histIdx > 0) {
      histIdx--;
      input.value = history[history.length - 1 - histIdx] ?? '';
    } else {
      histIdx = -1;
      input.value = '';
    }
    return;
  }
  if (e.key !== 'Enter') return;

  const cmd = input.value.trim();
  if (!cmd) return;

  appendCmd(cmd);
  history.push(cmd);
  histIdx = -1;
  input.value = '';

  if (cmd === 'clear') {
    output.innerHTML = '';
    return;
  }

  if (cmd === 'help') {
    appendOut(await helpText(), false);
    return;
  }

  input.disabled = true;
  document.getElementById('prompt-line').classList.add('busy');

  const params = new URLSearchParams();
  params.append('command', cmd);

  try {
    const resp = await fetch('/execute', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-XSRF-TOKEN': csrfToken()
      },
      body: params.toString()
    });

    if (resp.status === 401 || resp.status === 403) {
      window.location.href = '/login.html';
      return;
    }

    const text = await resp.text();
    appendOut(text, !resp.ok);
  } catch (err) {
    appendOut(err.message, true);
  }

  input.disabled = false;
  document.getElementById('prompt-line').classList.remove('busy');
  input.focus();
});

terminal.addEventListener('click', () => input.focus());
