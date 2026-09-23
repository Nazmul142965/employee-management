// Modern UI helper script
document.addEventListener('DOMContentLoaded', () => {
    // Generate avatar background gradients based on name initials
    const colors = [
        ['#4f46e5', '#7c3aed'],
        ['#2563eb', '#38bdf8'],
        ['#059669', '#34d399'],
        ['#d97706', '#fbbf24'],
        ['#e11d48', '#fb7185'],
        ['#7c2d12', '#f97316'],
        ['#4338ca', '#6366f1'],
        ['#0891b2', '#22d3ee']
    ];

    function hashString(str) {
        let hash = 0;
        for (let i = 0; i < str.length; i++) {
            hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        return Math.abs(hash);
    }

    document.querySelectorAll('[data-avatar-name]').forEach(el => {
        const name = el.getAttribute('data-avatar-name') || 'User';
        const parts = name.trim().split(/\s+/);
        let initials = parts[0] ? parts[0][0].toUpperCase() : 'U';
        if (parts.length > 1) {
            initials += parts[parts.length - 1][0].toUpperCase();
        }
        el.textContent = initials;
        
        const palette = colors[hashString(name) % colors.length];
        el.style.background = `linear-gradient(135deg, ${palette[0]}, ${palette[1]})`;
    });

    // Password visibility toggle helper
    document.querySelectorAll('.password-toggle').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const input = btn.closest('.input-with-icon').querySelector('input');
            if (input.type === 'password') {
                input.type = 'text';
                btn.innerHTML = '<i class="bi bi-eye-slash"></i>';
            } else {
                input.type = 'password';
                btn.innerHTML = '<i class="bi bi-eye"></i>';
            }
        });
    });
});

