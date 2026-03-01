(function () {
    'use strict';

    var currentStep = 1;
    var totalSteps = 3;

    function goToStep(step) {
        if (step < 1 || step > totalSteps) return;
        currentStep = step;

        document.querySelectorAll('.bank-step-panel').forEach(function (panel) {
            panel.classList.remove('active');
            if (parseInt(panel.getAttribute('data-step'), 10) === step) {
                panel.classList.add('active');
            }
        });

        document.querySelectorAll('.bank-stepper-item').forEach(function (item) {
            var itemStep = parseInt(item.getAttribute('data-step'), 10);
            item.classList.remove('active', 'done');
            if (itemStep === step) {
                item.classList.add('active');
            } else if (itemStep < step) {
                item.classList.add('done');
            }
        });

        var formStep = document.getElementById('formStep');
        if (formStep) formStep.value = step;
    }

    function updateInstallmentPreview() {
        var amountEl = document.getElementById('loanAmount');
        var termEl = document.getElementById('termMonths');
        var previewEl = document.getElementById('installmentPreview');
        if (!amountEl || !termEl || !previewEl) return;
        var amount = parseFloat(amountEl.value, 10);
        var term = parseInt(termEl.value, 10);
        if (term > 0 && amount > 0) {
            var monthly = amount / term;
            previewEl.textContent = Math.round(monthly).toLocaleString('tr-TR');
        } else {
            previewEl.textContent = '–';
        }
    }

    function setupEmploymentHelp() {
        var select = document.getElementById('employmentStatus');
        var helpIncome = document.getElementById('helpMonthlyIncome');
        var helpEmployer = document.getElementById('helpEmployer');
        if (!select || !helpIncome) return;
        function update() {
            var v = select.value;
            if (v === 'SELF_EMPLOYED') {
                helpIncome.textContent = 'Aylık ortalama gelirinizi girin.';
                if (helpEmployer) helpEmployer.textContent = 'Kendi işiniz veya unvanınız.';
            } else if (v === 'UNEMPLOYED') {
                helpIncome.textContent = 'Varsa diğer gelir kaynaklarınızı girin.';
                if (helpEmployer) helpEmployer.textContent = 'Boş bırakabilirsiniz.';
            } else {
                helpIncome.textContent = 'Aylık brüt gelirinizi girin.';
                if (helpEmployer) helpEmployer.textContent = 'Çalıştığınız kurum veya kendi işiniz.';
            }
        }
        select.addEventListener('change', update);
        update();
    }

    document.getElementById('btnNext1') && document.getElementById('btnNext1').addEventListener('click', function () {
        goToStep(2);
    });
    document.getElementById('btnNext2') && document.getElementById('btnNext2').addEventListener('click', function () {
        goToStep(3);
    });
    document.getElementById('btnBack2') && document.getElementById('btnBack2').addEventListener('click', function () {
        goToStep(1);
    });
    document.getElementById('btnBack3') && document.getElementById('btnBack3').addEventListener('click', function () {
        goToStep(2);
    });

    var loanAmount = document.getElementById('loanAmount');
    var termMonths = document.getElementById('termMonths');
    if (loanAmount) loanAmount.addEventListener('input', updateInstallmentPreview);
    if (termMonths) termMonths.addEventListener('input', updateInstallmentPreview);
    updateInstallmentPreview();
    setupEmploymentHelp();

    document.getElementById('nationalId') && document.getElementById('nationalId').addEventListener('input', function () {
        this.value = this.value.replace(/\D/g, '').slice(0, 11);
    });
})();
