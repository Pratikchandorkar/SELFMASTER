/**
 * Self-improvement - Core JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    initAnimations();
    initNavbar();
});

/* ---- Intersection Observer for scroll animations ---- */
function initAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.card, .stat-card').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'all 0.5s ease';
        observer.observe(el);
    });
}

/* ---- Navbar scroll effect ---- */
function initNavbar() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.style.background = 'rgba(10, 10, 15, 0.95)';
            navbar.style.borderBottomColor = 'rgba(108, 92, 231, 0.15)';
        } else {
            navbar.style.background = 'rgba(10, 10, 15, 0.85)';
            navbar.style.borderBottomColor = 'rgba(255, 255, 255, 0.06)';
        }
    });
}

/* ---- Toast Notification System ---- */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.style.cssText = 'position:fixed;top:80px;right:24px;z-index:9999;min-width:300px;animation:fadeInUp 0.3s ease;';
    toast.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i><span>${message}</span>`;
    document.body.appendChild(toast);
    setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 300); }, 3000);
}

/* ---- API Helper ---- */
async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin'
    };
    if (body) options.body = JSON.stringify(body);

    try {
        const response = await fetch(url, options);
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Request failed');
        return data;
    } catch (error) {
        showToast(error.message, 'error');
        throw error;
    }
}

/* ---- Format helpers ---- */
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function formatScore(score) {
    if (score >= 80) return { class: 'text-gradient', label: 'Excellent' };
    if (score >= 60) return { class: 'text-accent', label: 'Good' };
    if (score >= 40) return { class: '', label: 'Average' };
    return { class: 'text-muted', label: 'Needs Work' };
}
