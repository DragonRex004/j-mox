const API_BASE = "/api";

const UI = {
    loginPage: document.getElementById('login-page'),
    dashboard: document.getElementById('main-dashboard'),
    showDashboard: () => {
        UI.loginPage.classList.add('hidden');
        UI.dashboard.classList.remove('hidden');
    },
    showLogin: () => {
        UI.loginPage.classList.remove('hidden');
        UI.dashboard.classList.add('hidden');
    }
};

async function loadVMs() {
    const container = document.getElementById('vm-list-container');

    try {
        const response = await fetch(`${API_BASE}/vms`);
        const vms = await response.json();

        if (vms.length === 0) {
            container.innerHTML = `<tr><td colspan="3" class="px-6 py-10 text-center text-gray-500">No VMs found</td></tr>`;
            return false;
        }

        container.innerHTML = vms.map(vm => `
            <tr class="hover:bg-gray-800/30 transition-colors group">
                <td class="px-6 py-5">
                    <div class="flex items-center">
                        <div class="w-2 h-2 rounded-full ${vm.running ? 'bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.5)]' : 'bg-red-500'} mr-3"></div>
                        <span class="font-mono ${vm.running ? 'text-purple-300' : 'text-gray-400'}">${vm.name}</span>
                    </div>
                </td>
                <td class="px-6 py-5">
                    <span class="text-xs ${vm.running ? 'text-green-400 border-green-500/30' : 'text-red-400 border-red-500/30'} bg-gray-800 px-2 py-1 rounded border uppercase">
                        ${vm.running ? 'Running' : 'Stopped'}
                    </span>
                </td>
                <td class="px-6 py-5 text-right space-x-3">
                    <button onclick="controlVM('${vm.name}', 'start')"
                        ${vm.running ? 'disabled' : ''}
                        class="disabled:opacity-20 disabled:cursor-not-allowed bg-green-600/10 hover:bg-green-600 text-green-500 hover:text-white border border-green-600/30 px-4 py-1.5 rounded-md text-sm transition-all">
                        Start
                    </button>
                    <button onclick="controlVM('${vm.name}', 'stop')"
                        ${!vm.running ? 'disabled' : ''}
                        class="disabled:opacity-20 disabled:cursor-not-allowed bg-red-600/10 hover:bg-red-600 text-red-500 hover:text-white border border-red-600/30 px-4 py-1.5 rounded-md text-sm transition-all">
                        Stop
                    </button>
                </td>
            </tr>
        `).join('');

        return true;

    } catch (error) {
        console.error("Error by Loading VM List:", error);
        container.innerHTML = `<tr><td colspan="3" class="px-6 py-4 text-red-400 text-center">Server not answering</td></tr>`;
        return false;
    }
}

async function controlVM(name, action) {
    try {
        const response = await fetch(`${API_BASE}/vms/${name}/${action}`, { method: 'POST' });
        const message = await response.text();
        console.log(`Action ${action} for ${name}: ${message}`);
        setTimeout(loadVMs, 1000);
    } catch (error) {
        alert("Error by Executing Action: " + error);
    }
}

async function updateStats() {
    try {
        const response = await fetch(`${API_BASE}/stats`);
        const stats = await response.json();

        document.getElementById('cpu-val').innerText = `${stats.cpu}%`;
        document.getElementById('cpu-bar').style.width = `${stats.cpu}%`;

        document.getElementById('ram-val').innerText = `${stats.ram}%`;
        document.getElementById('ram-bar').style.width = `${stats.ram}%`;

    } catch (error) {
        console.warn("Metrics can't be loaded at the moment..");
    }
}

async function attemptLogin() {
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;

    const res = await fetch('/api/login', {
        method: 'POST',
        body: JSON.stringify({user, password: pass}),
        headers: {'Content-Type': 'application/json'}
    });

    if (res.ok) {
        UI.showDashboard();
        initPolling();
    } else {
        document.getElementById('login-error').classList.remove('hidden');
    }
}

async function checkAuthStatus() {
    const res = await fetch(`${API_BASE}/vms`);
    if (res.ok) {
        UI.showDashboard();
        initPolling();
    } else {
        UI.showLogin();
    }
}

function initPolling() {
    loadVMs();
    loadUsers();
    updateStats();
    setInterval(updateStats, 3000);
    setInterval(loadVMs, 5000);
}

async function logout() {
    if (!confirm("Do you really want to log out?")) return;
    try {
        await fetch(`${API_BASE}/logout`, { method: 'POST' });
        UI.showLogin();
        window.location.reload();
    } catch (err) {
        console.error("Logout failed:", err);
    }
}

async function loadUsers() {
    const res = await fetch('/api/users');
    const users = await res.json();
    const container = document.getElementById('user-list-container');

    container.innerHTML = users.map(user => `
        <tr class="group hover:bg-white/5 transition-all">
            <td class="py-4 px-2 font-medium text-gray-300">${user}</td>
            <td class="py-4 px-2 text-right space-x-2">
                <button onclick="editUser('${user}')" class="text-xs text-purple-400 hover:text-purple-300 uppercase font-bold tracking-widest p-2">Passwort ändern</button>
                ${user !== 'admin' ?
                    `<button onclick="deleteUser('${user}')" class="text-xs text-red-500 hover:text-red-400 uppercase font-bold tracking-widest p-2">Löschen</button>`
                    : '<span class="text-xs text-gray-600 p-2 italic">System-Admin</span>'}
            </td>
        </tr>
    `).join('');
}

async function deleteUser(name) {
    if (!confirm(`Should user "${name}" really be deleted?`)) return;
    await fetch(`/api/users/${name}`, { method: 'DELETE' });
    loadUsers();
}

async function editUser(name) {
    const newPass = prompt(`New Password for ${name}:`);
    if (!newPass) return;
    await fetch(`/api/users/${name}`, {
        method: 'PATCH',
        body: JSON.stringify({password: newPass}),
        headers: {'Content-Type': 'application/json'}
    });
    alert("Password is updated.");
}

document.addEventListener('DOMContentLoaded', checkAuthStatus);