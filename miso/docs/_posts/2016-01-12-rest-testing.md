---
layout: page
title: "REST Testing"
category: dev
date: 2016-01-12 13:49:31
---


## Manually Testing REST Resources During Development

During development a developer can set the constant `UNAUTHENTICATED_MODE` to `true` allowing REST resources to be accessed without authentication. This is useful for manual testing and exploration. This constant is found in the class `uk.ac.bbsrc.tgac.miso.webapp.context.RestSignatureHeaderFilter` and should only be set to `true` during development. A second constant, `UNAUTHENTICATED_MODE_USER`, found in the same class specifies which existing user resources will be associated with when created or modified in `UNAUTHENTICATED_MODE`.

```java
public class RestSignatureHeaderFilter extends OncePerRequestFilter {
...
  /** Used during development only. Set this to true to use REST resources without authentication. Good for manual testing/exploration. */
  private static boolean UNAUTHENTICATED_MODE = true;
  /** Resources created (POST) and modified (PUT) will be associated with this user in UNAUTHENTICATED_MODE. This user must exist. */
  private static String UNAUTHENTICATED_MODE_USER = "admin";
...
}
```

When `UNAUTHENTICATED_MODE` is set to `true` the following message will be displayed in the error log as a reminder that `UNAUTHENTICATED_MODE` is active`.`

```
**************************************************************************************
**  DANGER!! REST requests in MISO are currently unauthenticated. This is suitable  **
**  during development only. Adjust setting in RestSignatureHeaderFilter class.     **
**************************************************************************************
```

## Postman

Postman is a browser extension to test and exercise REST web service APIs. Postman can be downloaded here: [https://www.getpostman.com/](https://www.getpostman.com/)

Here is a collection of MISO REST urls in the file [miso-rest.json.postman_collection](/download/attachments/10420483/miso-rest.json.postman_collection?version=1&modificationDate=1449691946000&api=v2) that you can download and import into Postman. The url (host and port) is specified as a Postman environment variable so you will need to set one up as will in order to use these urls.

Here an environment called "localhost" has been created. It contains one environment variable "url" which in this case has the value "http://localhost:8080". When setting this up make sure the value points to the particular instance of MISO being tested.
 ![Postman]({{ site.baseurl }}/images/postman.png)
