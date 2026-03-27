## How the Feed Works End to End
```
GET /api/public/feed                     → all jobs, merged
GET /api/public/feed?source=COMMUNITY    → only community submissions
GET /api/public/feed?source=RECRUITER_DIRECT → only easy-apply jobs
GET /api/public/feed?domain=FINANCE      → all finance jobs across both types
GET /api/public/feed?domain=TECHNOLOGY&techRole=BACKEND_ENGINEER
GET /api/public/feed?location=bangalore&minExperience=2
```

Frontend reads `easyApply` flag on each item:
```
easyApply = true  → "Apply on IITBase" button → hits applications module
easyApply = false → "Visit Company Site" button → opens applyUrl
verifiedCompany   → show blue "Verified" badge next to company name