if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.submission = (function($) {

  return {
    getSaveUrl: function(submission) {
      return submission.id ? ('/miso/rest/submissions/' + submission.id) : '/miso/rest/submissions';
    },
    getSaveMethod: function(submission) {
      return submission.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(submission) {
      return '/miso/submission/' + submission.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Submission Information',
        fields: [{
          title: 'Submission ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(submission) {
            return submission.id || 'Unsaved';
          }
        }, {
          title: 'Title',
          data: 'title',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 100
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Accession',
          data: 'accession',
          type: 'read-only',
          getLink: function(sample) {
            return 'http://www.ebi.ac.uk/ena/data/view/' + sample.accession;
          },
          include: object.accession
        }, {
          title: 'Completed?',
          data: 'completed',
          type: 'checkbox'
        }, {
          title: 'Verified?',
          data: 'verified',
          type: 'checkbox'
        }, {
          title: 'Submission Date',
          data: 'submittedDate',
          type: 'date'
        }]
      }];
    }
  }

})(jQuery);
