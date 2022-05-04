# Testcases

- [ ] memory leak check (Are objects deleted upon session closing)
- [ ] E2E
  - [ ] basic tests
    - [ ] set tooltip
    - [ ] update tooltip
    - [ ] remove tooltip
    - [ ] hide tooltip
    - [ ] unhide tooltip
      - [ ] update while hidden
    - [ ] detach and attach component
      - [ ] update while detached
    - [ ] setting a tooltip to an initially hidden element
  - [ ] HasTooltip checks (the basic ones above up until unhide)
  - [ ] tooltip configuration tests
    - [ ] default config
    - [ ] overwrite default config
  - [ ] setting tooltips to table elements and scroll beneath the first page
  - [ ] load test
  - [ ] fault tolerant deletion


# Performance Optimization

- [ ] Check if there are pending js calls that are overwritten (e.g. updates) and can be canceled 


# Demo

- [x] separate the project into submodules (lib, demo, test)
- [ ] publish public demo
