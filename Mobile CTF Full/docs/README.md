# Maintainer docs (staged for openlabs)

These 7 files satisfy sub-issue #161's "seven-document pattern where
applicable" checklist item, matching the exact file-count/naming pattern
of `labs/web/firmdrama/docs/` (the only existing lab in the catalog with
this doc convention) — the same 7 names, scaled to a BYOD artifact-only
mobile lab with no server-side component:

- `README.md` (this file)
- `challenge-design.mdx`
- `official-solution.mdx`
- `operations.mdx`
- `remediation.mdx`
- `security-report.mdx`
- `validation-report.mdx`

Reorganized from the original narrative write-ups under
`../documentation/` (`Objective.md`, `Walkthrough.md`, `What Happened and
Why (Root Cause).md`, `Mitigation.md`). `Public Briefing.md` is kept
as-is there — it's the player-facing brief and isn't duplicated here.

`labs/binary/` doesn't exist yet anywhere in `duckurity/openlabs` as of
this writing — `dual-check` (sub-issue #162) and this lab are expected to
be among the catalog's first binary-track labs, each landing in its own
PR per the maintainer's note on issue #161.

Staged here only, per the standing rule: nothing gets pushed to
`duckurity/openlabs` until all 3 sub-issues are agreed with the
maintainer. Final home once agreed:

```
labs/binary/nexawallet/docs/README.md
labs/binary/nexawallet/docs/challenge-design.mdx
labs/binary/nexawallet/docs/official-solution.mdx
labs/binary/nexawallet/docs/operations.mdx
labs/binary/nexawallet/docs/remediation.mdx
labs/binary/nexawallet/docs/security-report.mdx
labs/binary/nexawallet/docs/validation-report.mdx
```

See `validation-report.mdx` for what is and isn't verified yet — this lab
is not shippable until the author completes the local-only steps listed
there (no Android SDK is available in the environment these docs were
authored in).
