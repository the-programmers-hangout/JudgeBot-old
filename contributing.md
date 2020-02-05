### A word on our pull request policy
It is in our best interests to keep the code around here very clean.
This means that while we will most certainly make an effort to add a few comments
on pull requests where necessary, if the code is particularly out of line
or if it needs a lot of conversation, that will be discussed over voice! 
If you wish to contribute here, please keep that in mind :) 
If a voice call is not possible for you, we can try to do it over the comments
but this is not optimal. If you just have a small, atomic change to 
make this won't be necessary, but if you make big sweeping changes, expect 
to either have to jump on a voice call over discord, or have it rejected.


### Do
- Keep pull requests and commits atomic
- Ensure commit messages are informative, prefer to say "why" not "what"
- Hard to understand pieces of code are documented, and preferably tested
- Merge requests made are discussed at the issue level first, don't 
just randomly PR some crap and expect it to be accepted.

### Don't
- Make large sweeping pull requests that change the style of code, e.g. new line braces
or changing a bunch of things to .let/.apply or vice versa. These will be rejected immediately.
You are a developer, you are not a linter. 
- Forget to document or clearly flag breaking changes.
- Forget to test the bot at the jar and docker levels.

In general, if you want to contribute to the project it's no big deal, just
keep your MR small, focused, clean, and try not to disturb to much code. 
If it's clear that something needs to be refactored we can open an issue about it
and move from there.