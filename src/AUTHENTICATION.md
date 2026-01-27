What Startups Actually Use (Real Examples)
Early Stage (0-10K users) - 80% of startups:
Approach: Simple JWT with short expiry OR JWT + Redis blacklist
Examples:

Linear (Early days): Stateless JWT
Cal.com (Open source): JWT + Redis
Supabase: JWT with short expiry
Most YC startups: Start with stateless JWT

Why: Speed to market > perfect architecture
Growth Stage (10K-100K users) - Most migrate here:
Approach: JWT (Access) + Redis (Refresh tokens)
Examples:

Vercel: JWT + Redis for session management
Stripe: JWT + Redis for high-performance validation
Notion: Token-based with Redis caching
Discord: Redis-backed session store

Why: Redis = Fast + Scalable + Simple
Scale Stage (100K+ users):
Approach: Distributed sessions with Redis Cluster OR Custom auth infrastructure
Examples:

Airbnb: Custom distributed auth
Uber: Redis Cluster + custom logic
Netflix: Custom Zuul + distributed sessions


Redis Approach (Recommended for Startups)
Why Redis for Tokens?
Speed Comparison:
├─ PostgreSQL query: ~5-10ms
├─ Redis lookup:      ~0.1ms  ← 50-100x faster!
└─ In-memory check:   ~0.01ms
Benefits:

✅ Blazing fast (sub-millisecond lookups)
✅ TTL built-in (auto-expire tokens)
✅ Scalable (Redis Cluster for millions of users)
✅ Simple (key-value store, no complex queries)
✅ Battle-tested (used by Stripe, GitHub, Airbnb)


Implementation: Redis-Based Token Management
Architecture:
┌──────────┐                 ┌──────────┐                 ┌─────────┐
│ Frontend │ ──── Token ───> │ Backend  │ ──── Check ──> │  Redis  │
│          │                 │          │                 │         │
│          │                 │ Validate │                 │ Active  │
│          │ <─── Response ─ │ Signature│ <─── Result ─  │ Tokens  │
└──────────┘                 └──────────┘                 └─────────┘
│
└──> PostgreSQL (User data only)
Strategy 1: Redis Whitelist (Most Common for Startups)
Store active tokens in Redis. If not in Redis = invalid.