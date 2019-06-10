#!/bin/bash

set -euo pipefail

DOCS_BRANCH=gh-pages

TEMP_CONFIG=_version_config.yml
TEMP_BRANCH=temp_docs
START_BRANCH=$(git rev-parse --abbrev-ref HEAD)

git fetch --tags

pushd docs
rm -rf ./_site

# Build versioned docs for each minor version
VERSIONS=($(git tag | grep '^v[0-9]*\.[0-9]*\.[0-9]*$' | cut -d . -f 1,2 - | sort | uniq))
for MINOR in ${VERSIONS[@]}
do
  PATCH=$(git tag | grep "^${MINOR}\.[0-9]*$" | cut -d . -f 3 | sort -n | tail -n1)
  echo -e "\nBuilding ${MINOR}.${PATCH} as '${MINOR}.x'..."
  git checkout ${MINOR}.${PATCH}
  V=${MINOR:1}.x
  echo "baseurl : /miso-lims/${V}" > ${TEMP_CONFIG}
  jekyll build --config _config.yml,${TEMP_CONFIG} -d _site/${V}/
done

# Add "latest" as copy of most recent version
echo -e "\nRebuilding ${MINOR}.${PATCH} as 'latest'..."
echo "baseurl : /miso-lims/latest" > ${TEMP_CONFIG}
jekyll build --config _config.yml,${TEMP_CONFIG} -d _site/latest/
rm ${TEMP_CONFIG}

# Add /index.html that redirects to /miso-lims/latest/
echo "<html>
  <head>
    <meta http-equiv=\"Refresh\" content=\"0; url=/miso-lims/latest/\" />
  </head>
  <body>
    <p>See the latest version of the docs <a href=\"/miso-lims/latest/\">here</a></p>
  </body>
</html>" > _site/index.html
popd

# push to GitHub docs branch
echo -e "\nDeploying to GitHub..."
git checkout -b ${TEMP_BRANCH}
rm .gitignore
git add docs/_site/
git commit -m "Built versioned site to ${MINOR}.${PATCH}"
git subtree split --prefix docs/_site -b gh-pages
git push -f origin gh-pages:gh-pages
git branch -D gh-pages
git checkout .gitignore
git checkout ${START_BRANCH}
git branch -D ${TEMP_BRANCH}

