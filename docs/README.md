# MISO Github pages

Documentation: https://miso-lims.readthedocs.io

MISO's documentation is built with [mkdocs](https://www.mkdocs.org/)
and hosted by [readthedocs](https://www.mkdocs.org/).


## Prerequisites

Install [mkdocs](https://www.mkdocs.org/#installation).


## Test and deploy locally

To test out changes locally, run:

```
mkdocs serve -f .mkdocs.yml
```

The pages will be likely be hosted at [http://127.0.0.1:8000](http://127.0.0.1:8000),
though be sure to check the output in case it hosts at another address or port.


## Readthedocs configuration

Configuration for readthedocs is located in
[../.readthedocs.yml](../.readthedocs.yml). It should build automatically.
Launch a build manually (or do other configuration) by going to
[https://readthedocs.org/projects/miso-lims/](https://readthedocs.org/projects/miso-lims/).
