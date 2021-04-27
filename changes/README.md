# Writing Changes

All notable changes to this project should be documented. Changes should be written to files in
this directory, which will be merged into the changelog during release via `compact-changelog.sh`.

## Change Types

The following change types are supported. Changes will be listed by type in the changelog, and the
types of changes contained in a release determine the release type. If there are any changes
requiring a minor release, a minor release should be performed. If there are only changes that
require a patch release, then a patch release should be performed instead.

| Change Type | Filename Prefix | Description | Release Type |
|-------------|-----------------|-------------|--------------|
| Added       | `add_`          | New features | minor |
| Changed     | `change_`       | Changes in existing functionality | minor |
| Removed     | `remove_`       | Features that have been removed | minor |
| Fixed       | `fix_`          | Bug fixes and performance improvements | patch |

## File Names

The file name must begin with a prefix depending on the change type (see above), followed by an
underscore. e.g. `fix_saving_samples.md` or `add_GLT-1234.md`. The portion after the underscore
is not important, but something descriptive makes them easier to find if they need to be modified.

## Message Format

Each change must be in a separate file, but a single change may contain multiple lines of text.
Changes can be written using markdown. A bullet point will be added before the first line when
writing to the changelog, so the first line in a change file should not have a bullet point.
Subsequent lines will be indented, so you could include a second-level list by using bullet points.
For example, the following is a well-formatted change, which could be named `add_field_x.md`:

```
Field x on several entities
* samples
* libraries
* runs
```

## Upgrade Notes

When migration to a new version requires additional maintenance/administrative work, this should be
noted separately. These upgrade notes can be added in files starting with `note_`. The note should
be in addition to one or more of the other change types above.

## Known Issues

When major issues are discovered in released versions, they should be noted in the changelog
section for any affected release. These should be added directly to the changelog file.
