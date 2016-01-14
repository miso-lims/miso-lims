#MISO Github pages

MISO's project website is built with Jekyll and intended to be hosted on Github pages.

The page template is a slightly modified version of [jekyll-docs-template](http://bruth.github.io/jekyll-docs-template).


##Installing

For Ubuntu, install jekyll directly (don't mess around with Ruby versions and Gem and Jekyll versions -- that way leads to madness.

    sudo apt-get install jekyll


##Building locally and testing

The project is configured to run on Github, so build and serve the website like this during testing

    jekyll build ; jekyll serve --baseurl ''


## Deploying to gh-pages

First, build the site locally without modifying baseurl.

    jekyll build


Add and commit the _site subdirectory and then push the subtree to gh-pages from the root directory.

    git add docs/_site/* && git commit -m "Rebuilding the website"
    git subtree push --prefix docs/_site oicr gh-pages    

For troubleshooting, see [Deploying a subfolder to Github Pages](https://gist.github.com/cobyism/4730490).


##Cheat sheet

###Code blocks

As per Github-flavoured markdown, code blocks can either have each line preceeded by at least four spaces or be surrounded by back-tick blocks.

    ```
    This is a code block
    ```

###Link to a post

    [REST API]({{ site.baseurl }}{% post_url 2016-01-12-rest-api %})


(no slash between baseurl and posturl)

###Display an image

    ![MISO model interfaces]({{ site.baseurl }}/images/core_model.png)

(include slash between baseurl and image)
