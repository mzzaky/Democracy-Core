# Democracy Core - PlaceholderAPI Quick Reference

## Global Placeholders (No Player Context Required)

### Government
- `%democracy_president%` - Current president name
- `%democracy_term_time%` - Remaining term time
- `%democracy_approval_rating%` - President approval rating (1-5)
- `%democracy_consecutive_terms%` - Consecutive terms count
- `%democracy_salary_payouts%` - Total salary paid this term

### Cabinet Ministers
- `%democracy_defense_minister%` - Defense Minister name
- `%democracy_treasury_minister%` - Treasury Minister name
- `%democracy_commerce_minister%` - Commerce Minister name
- `%democracy_infrastructure_minister%` - Infrastructure Minister name
- `%democracy_culture_minister%` - Culture Minister name

### Election
- `%democracy_phase%` - Current election phase
- `%democracy_phase_time%` - Time remaining in phase
- `%democracy_election_active%` - Is election active?
- `%democracy_candidates_count%` - Number of candidates
- `%democracy_total_votes%` - Total votes cast

### Treasury
- `%democracy_treasury%` - Current treasury balance
- `%democracy_treasury_income%` - Total income
- `%democracy_treasury_expenses%` - Total expenses

---

## Player-Specific Placeholders (Requires Player Context)

### Government Roles
- `%democracy_is_president%` - Is player president?
- `%democracy_is_cabinet%` - Is player cabinet member?
- `%democracy_cabinet_position%` - Player's cabinet position

### Lifetime Statistics
- `%democracy_votes_cast%` - Total votes cast (lifetime)
- `%democracy_times_ran%` - Times ran for president
- `%democracy_times_president%` - Times served as president
- `%democracy_times_cabinet%` - Times served as cabinet
- `%democracy_endorsements_given%` - Endorsements given
- `%democracy_endorsements_received%` - Endorsements received
- `%democracy_total_donations%` - Total donations to treasury
- `%democracy_playtime_hours%` - Total playtime in hours

### Current Election
- `%democracy_has_voted%` - Has voted in current election?
- `%democracy_is_candidate%` - Is player a candidate?
- `%democracy_candidate_votes%` - Votes received (if candidate)
- `%democracy_candidate_endorsements%` - Endorsements received (if candidate)
- `%democracy_candidate_slogan%` - Candidate slogan

### Arena Statistics
- `%democracy_arena_kills%` - Total arena kills
- `%democracy_arena_deaths%` - Total arena deaths
- `%democracy_arena_kd%` - Kill/Death ratio
- `%democracy_killstreak%` - Current killstreak
- `%democracy_best_killstreak%` - Best killstreak

---

**Total Placeholders**: 40+

**Usage**: Replace `%placeholder%` in any PlaceholderAPI-compatible plugin.

**Test Command**: `/papi parse me %democracy_president%`
