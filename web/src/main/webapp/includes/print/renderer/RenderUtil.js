var RenderUtil = Backbone.Model.extend({}, {

    templates: {},

    loadTemplates: function (names, callback) {
        var that = this;

        var loadTemplate = function (index) {
            var name = names[index];
            debug('Loading template: ' + name, util_logDebug);
            $.get(app_contextPath + '/template/' + name + '.html', function (data) {
                that.templates[name] = data;
                index++;
                if (index < names.length) {
                    loadTemplate(index);
                } else {
                    callback();
                }
            });
        }

        loadTemplate(0);
    },

    get: function (name) {
        return this.templates[name];
    },

    render: function (srcTemplate, values) {
        return $.tmpl(srcTemplate, values);
    }
});
