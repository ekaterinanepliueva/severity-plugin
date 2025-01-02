const SeverityWidgetModel = Backbone.Model.extend({
    url: 'widgets/severity-table.json',

    parse(response) {
        const data = Object.entries(response).reduce((acc, [val, count]) => {
            acc[val] = count;

            return acc;
        }, {});

        return {data};
    }
});

class SeverityWidget extends Backbone.Marionette.View {
    template() {
        return `
            <h2 class="severity widget__title">Tests by severity</h2>
            <div class="table table_hover widget__table">
                <div class="severity-total"></div>
                <div class="severity-items"></div>
            </div>
        `;
    }

    initialize() {
        this.model = new SeverityWidgetModel();
        this.model.fetch();
        this.addStyles();
    }

    onRender() {
        const data = this.model.get('data') || {};
        const { total } = data;

        const itemsHtml = Object.entries(data)
            .filter(([key]) => key !== 'total')
            .map(([severity, count]) => {
                const severityLabel = severity.charAt(0).toUpperCase() + severity.slice(1);

                return `
                <div class="severity-item">
                    <div class="severity-icon" data-status="${severityLabel}"></div>
                    <a href="#suites" class="severity-label">${severityLabel} - ${count}%</a>
                </div>
            `;
            })
            .join('');

        this.$('.severity-total').html(`Total: ${total}`);
        this.$('.severity-items').html(itemsHtml);
    }

    addStyles() {
        if (!document.getElementById('severity-widget-css')) {
            const link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = "plugin/severity/styles.css";
            link.id = "severity-widget-css";
            document.head.appendChild(link);
        }
    }
}

allure.api.addWidget('widgets', 'severity-table', SeverityWidget);
