IndexChecker = (function($) {

  const DEFAULT_MIN_DISTANCE = 3;

  return {
    calculate: function() {
      const indices = getIndicesInput();
      const minDistance = getMinDistanceInput().value;
      
      if (!indices.length || indices.length < 2) {
        showError('Enter at least two indices');
      } else if (!/^\d$/.test(minDistance) || minDistance < 1 || minDistance > 4) {
        showError('Enter a minimum distance between 1 and 4');
      } else {
        clearResults();
        
        const shortestIndexLength = getShortestLength(indices);
        
        let errors = [];
        for (let i = 0; i < indices.length - 1; i++) {
          for (let j = i+1; j < indices.length; j++) {
            if (!errors.find(function(error) {
              return (error.index1 === indices[i] && error.index2 === indices[j])
                  || (error.index1 === indices[j] && error.index2 === indices[i])
            })) {
              let editDistance = getEditDistance(indices[i], indices[j], shortestIndexLength);
              if (editDistance < minDistance) {
                errors.push({
                  index1: indices[i],
                  index2: indices[j],
                  distance: editDistance
                });
              }
            }
          }
        }
        let results = 'Indices were compared at the length of the shortest index, which is ' + shortestIndexLength + ' bp.\n';
        if (errors.length) {
          errors.forEach(function(error) {
            results += '\n';
            if (error.index1 === error.index2) {
              results += 'Duplicate: ' + error.index1;
            } else {
              results += 'Near-duplicates: "' + error.index1 + '", "' + error.index2 + '"';
            }
          });
          showError(results);
        } else {
          results += '\nAll sequences are at least ' + minDistance + ' edits apart';
          getResultsBox().style.color = 'green';
          getResultsBox().value = results;
        }
      }
    },
    resetForm: function() {
      getIndicesBox().value = '';
      getMinDistanceInput().value = DEFAULT_MIN_DISTANCE;
      clearResults();
    }
  };
  
  function clearResults() {
    getResultsBox().value = '';
  }
  
  function getIndicesBox() {
    return document.getElementById('indices');
  }
  
  function getMinDistanceInput() {
    return document.getElementById('min-distance');
  }
  
  function getResultsBox() {
    return document.getElementById('results');
  }

  function showError(message) {
    getResultsBox().style.color = 'red';
    getResultsBox().value = message;
  }

  function getIndicesInput() {
    return getIndicesBox().value.split('\n').map(function(val) {
      // remove spaces or other characters used to separate dual index sequences
      return val.replace(/\W/g, '');
    }).filter(function(val) {
      return !!val;
    });
  }
  
  function getShortestLength(strings) {
    return strings.reduce(function(accumulator, currentValue) {
      return accumulator ? Math.min(accumulator, currentValue.length) : currentValue.length;
    }, null);
  }
  
  function getEditDistance(index1, index2, shortestIndexLength) {
    let distance = shortestIndexLength;
    for (let i = 0; i < shortestIndexLength; i++) {
      if (index1.charAt(i) === index2.charAt(i)) {
        distance--;
      }
    }
    return distance;
  }

})();
