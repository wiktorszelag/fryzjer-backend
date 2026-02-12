document.addEventListener('DOMContentLoaded', () => {



    // --- Dynamic Mouse Effects ---
    const body = document.body;

    // Create spotlight element
    const spotlight = document.createElement('div');
    spotlight.classList.add('cursor-spotlight');
    document.body.appendChild(spotlight);

    // Track mouse movement for spotlight
    document.addEventListener('mousemove', (e) => {
        const x = e.clientX;
        const y = e.clientY;

        spotlight.style.background = `radial-gradient(600px circle at ${x}px ${y}px, rgba(230, 0, 0, 0.15), transparent 40%)`;

        // Update CSS variables for other potential effects
        body.style.setProperty('--mouse-x', `${x}px`);
        body.style.setProperty('--mouse-y', `${y}px`);
    });

    // --- 3D Tilt Effect for Service Cards ---
    const cards = document.querySelectorAll('.service-card');

    cards.forEach(card => {
        card.addEventListener('mousemove', (e) => {
            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            // Calculate rotation
            // Center is (rect.width/2, rect.height/2)
            // Max rotation is roughly +/- 10deg
            const centerX = rect.width / 2;
            const centerY = rect.height / 2;

            const rotateX = ((y - centerY) / centerY) * -10; // Invert logic for correct tilt (mouse down -> rotates up? No, standard tilt: top goes back)
            // Actually: mouse at top (y < centerY) -> rotateX should be positive (top comes forward/bottom goes back?? No wait).
            // CSS rotateX: positive = top goes back.
            // If mouse is at top, we want top to go BACK (pushed away). So positive rotateX.
            // (y - centerY) is negative. So we need to multiply by -1.

            const rotateY = ((x - centerX) / centerX) * 10;
            // CSS rotateY: positive = right goes back.
            // If mouse is right, we want right to go back. So positive rotateY.
            // (x - centerX) is positive. So simply multiply by 10.

            card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.02, 1.02, 1.02)`;
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) scale3d(1, 1, 1)';
        });
    });

    // Immediate reveal of hero content since preloader is gone
    const heroContent = document.querySelector('.hero-content');
    if (heroContent) {
        setTimeout(() => {
            heroContent.classList.add('visible');
        }, 100);
    }


    // --- Mobile Menu Toggle ---
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');

    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            const icon = menuToggle.querySelector('i');
            if (navLinks.classList.contains('active')) {
                icon.classList.remove('fa-bars');
                icon.classList.add('fa-times');
            } else {
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            }
        });
    }

    // --- Smooth Scrolling for Anchor Links ---
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();

            // Close mobile menu if open
            if (navLinks.classList.contains('active')) {
                navLinks.classList.remove('active');
                const icon = menuToggle.querySelector('i');
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            }

            const targetSection = document.querySelector(this.getAttribute('href'));
            if (targetSection) {
                targetSection.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    // --- Scroll Animations & Effects ---
    const navbar = document.querySelector('.navbar');
    const heroSection = document.querySelector('.hero');
    const heroOverlay = document.querySelector('.hero-overlay');
    const heroHeight = heroSection ? heroSection.offsetHeight : 0;

    window.addEventListener('scroll', () => {
        const scrollPos = window.scrollY;

        // 1. Navbar Appearance on Scroll
        if (scrollPos > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }

        // 2. Parallax / Darkening Effect on Hero
        // As we scroll down, opacity of overlay increases from 0.5 (css default) to 1
        // 2. Parallax Effect on Hero Text
        if (scrollPos <= heroHeight) {
            // Optional: Move content slightly for parallax effect on text
            if (heroContent) {
                heroContent.style.transform = `translateY(${scrollPos * 0.4}px)`;
                heroContent.style.opacity = 1 - (scrollPos / (heroHeight * 0.7));
            }
        }
    });


    // --- Intersection Observer for Fade-In Animations ---
    const observerOptions = {
        threshold: 0.15,
        rootMargin: "0px 0px -50px 0px"
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.service-card, .gallery-item, .section-title, .about-content').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'opacity 0.8s ease-out, transform 0.8s ease-out';
        observer.observe(el);
    });

    // Add animation class style dynamically
    const styleSheet = document.createElement("style");
    styleSheet.innerText = `
        .animate-in {
            opacity: 1 !important;
            transform: translateY(0) !important;
        }
    `;
    document.head.appendChild(styleSheet);
});
