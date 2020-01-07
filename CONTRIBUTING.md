# How to contribute

We're glad you're interested in contributing to the development of MISO and are happy to collaborate.
Please review this document to ensure that your work fits well into the MISO code base.

## Tickets

Create a ticket for your issue if one does not exist. As development of MISO is mainly done at OICR
currently, the ticket is usually on the internal OICR JIRA, but a GitHub Issue is also acceptable.
This ensures that we have a place to discuss the changes before the work is done. A ticket is not
necessary if the change is trivial, such as correcting a typo.

## Branches

Create a feature branch. The branch should be based on the `develop` branch unless you have reason
to do otherwise. The branch name should begin with the issue number, and be followed by a brief hint
of what it is about. e.g. "#1234_fixLibraryPage"

## Code Formatting

The project includes Eclipse code formatter settings for Java and JavaScript. Please ensure that
these are used. This keeps the diff clean, so it is easier to review your changes.

## Testing

We have several types of automated testing:

* Unit tests
  * Run with `mvn clean install`
* Integration tests in `sqlstore`
  * Run with `cd sqlstore; mvn clean verify -DskipITs=false`
* Integration tests in `miso-web` for plain sample mode
  * Run with `cd miso-web; mvn clean verify -DrunPlainITs`
* Integration tests in `miso-web` for detailed sample mode (also contains tests which are relevant to both modes)
  * Run with `cd miso-web; mvn clean verify -DskipITs=false`
  * Run individual test classes or methods by adding `-Dit.test=ClassName` or `Dit.test=ClassName#methodName`
* Integration tests for `pinery-miso`
  * Run with `cd pinery-miso; mvn clean verify -DskipITs=false`

Please make sure to add or update the relevant tests for your changes.

## Commits

* Make sure your commit messages begin with the issue number. e.g. "#1234: fixed display of
  library aliquot concentration units"
* Edit the **Unreleased** section in `RELEASE_NOTES.md` to detail any user-visible **Changes** or
  **Update Notes** (Additional steps that must be taken when upgrading to the MISO version
  containing your change)

## Pull Requests

Changes should never be merged directly into `develop` or `master`. Pull requests should be made into
the `develop` branch for testing and review. The `master` branch is only ever updated to point to the
latest official release.

## Merging

Once all of the tests are passing, and your pull request has received two approvals, you are ready to
merge. To keep a clean commit history please

1. Squash your changes into one commit unless they are clearly separate changes.
2. Rebase on the `develop` branch so that your change appears after the changes that were previously
merged in the history.

You can usually use the 'Squash and merge' feature on GitHub to do this. Use the 'Rebase and merge'
feature if you would like to keep the commits separate. If you have a more complex situation, such as
wanting to squash some commits but not others, you should use `git rebase` and then re-run tests
before merging. If you do not have the necessary permissions to merge into `develop`, please request
for someone else to do the merge.

Please delete your feature branch after it is merged.

### Thank you for your contributions!

