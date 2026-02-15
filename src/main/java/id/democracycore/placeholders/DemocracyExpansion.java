package id.democracycore.placeholders;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import id.democracycore.DemocracyCore;
import id.democracycore.models.Government.CabinetPosition;

public class DemocracyExpansion extends PlaceholderExpansion {

    private final DemocracyCore plugin;

    public DemocracyExpansion(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "DemocracyCore Team";
    }

    @Override
    public String getIdentifier() {
        return "democracy";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        // === GOVERNMENT PLACEHOLDERS ===
        if (params.equalsIgnoreCase("president")) {
            String name = plugin.getGovernmentManager().getGovernment().getPresidentName();
            return name != null ? name : "None";
        }

        if (params.equalsIgnoreCase("term_time")) {
            long time = plugin.getGovernmentManager().getTermRemainingTime();
            return formatTime(time);
        }

        if (params.equalsIgnoreCase("approval_rating")) {
            return String.format("%.1f", plugin.getGovernmentManager().getGovernment().getCurrentApprovalRating());
        }

        if (params.equalsIgnoreCase("consecutive_terms")) {
            return String.valueOf(plugin.getGovernmentManager().getGovernment().getConsecutiveTerms());
        }

        if (params.equalsIgnoreCase("salary_payouts")) {
            return String.format("%.2f", plugin.getGovernmentManager().getGovernment().getTotalSalaryPayouts());
        }

        // Cabinet member placeholders
        if (params.equalsIgnoreCase("defense_minister")) {
            return getCabinetMemberName(CabinetPosition.DEFENSE);
        }

        if (params.equalsIgnoreCase("treasury_minister")) {
            return getCabinetMemberName(CabinetPosition.TREASURY);
        }

        if (params.equalsIgnoreCase("commerce_minister")) {
            return getCabinetMemberName(CabinetPosition.COMMERCE);
        }

        if (params.equalsIgnoreCase("infrastructure_minister")) {
            return getCabinetMemberName(CabinetPosition.INFRASTRUCTURE);
        }

        if (params.equalsIgnoreCase("culture_minister")) {
            return getCabinetMemberName(CabinetPosition.CULTURE);
        }

        // === ELECTION PLACEHOLDERS ===
        if (params.equalsIgnoreCase("phase")) {
            if (plugin.getElectionManager().isElectionActive()) {
                return plugin.getElectionManager().getElection().getCurrentPhase().getDisplayName();
            }
            return "None";
        }

        if (params.equalsIgnoreCase("phase_time")) {
            long time = plugin.getElectionManager().getPhaseRemainingTime();
            return formatTime(time);
        }

        if (params.equalsIgnoreCase("candidates_count")) {
            return String.valueOf(plugin.getElectionManager().getElection().getCandidates().size());
        }

        if (params.equalsIgnoreCase("total_votes")) {
            return String.valueOf(plugin.getElectionManager().getElection().getTotalVotes());
        }

        if (params.equalsIgnoreCase("election_active")) {
            return plugin.getElectionManager().isElectionActive() ? "Yes" : "No";
        }

        // === TREASURY PLACEHOLDERS ===
        if (params.equalsIgnoreCase("treasury")) {
            return String.format("%.2f", plugin.getTreasuryManager().getBalance());
        }

        if (params.equalsIgnoreCase("treasury_income")) {
            return String.format("%.2f", plugin.getTreasuryManager().getTotalIncome());
        }

        if (params.equalsIgnoreCase("treasury_expenses")) {
            return String.format("%.2f", plugin.getTreasuryManager().getTotalExpenses());
        }

        // === PLAYER-SPECIFIC PLACEHOLDERS ===
        if (player != null) {
            var playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());

            // Government role
            if (params.equalsIgnoreCase("is_president")) {
                return plugin.getGovernmentManager().isPresident(player.getUniqueId()) ? "Yes" : "No";
            }

            if (params.equalsIgnoreCase("is_cabinet")) {
                return plugin.getGovernmentManager().isCabinetMember(player.getUniqueId()) ? "Yes" : "No";
            }

            if (params.equalsIgnoreCase("cabinet_position")) {
                CabinetPosition pos = plugin.getGovernmentManager().getCabinetPosition(player.getUniqueId());
                return pos != null ? pos.getDisplayName() : "None";
            }

            // Player statistics (lifetime)
            if (playerData != null) {
                if (params.equalsIgnoreCase("votes_cast")) {
                    return String.valueOf(playerData.getTotalVotesCast());
                }

                if (params.equalsIgnoreCase("times_ran")) {
                    return String.valueOf(playerData.getTimesRanForPresident());
                }

                if (params.equalsIgnoreCase("times_president")) {
                    return String.valueOf(playerData.getTimesServedAsPresident());
                }

                if (params.equalsIgnoreCase("times_cabinet")) {
                    return String.valueOf(playerData.getTimesServedAsCabinet());
                }

                if (params.equalsIgnoreCase("endorsements_given")) {
                    return String.valueOf(playerData.getEndorsementsGiven());
                }

                if (params.equalsIgnoreCase("endorsements_received")) {
                    return String.valueOf(playerData.getEndorsementsReceived());
                }

                if (params.equalsIgnoreCase("total_donations")) {
                    return String.format("%.2f", playerData.getTotalDonations());
                }

                if (params.equalsIgnoreCase("playtime_hours")) {
                    return String.format("%.1f", playerData.getPlaytimeHours());
                }

                // Arena statistics
                if (params.equalsIgnoreCase("arena_kills")) {
                    return String.valueOf(playerData.getArenaKills());
                }

                if (params.equalsIgnoreCase("arena_deaths")) {
                    return String.valueOf(playerData.getArenaDeaths());
                }

                if (params.equalsIgnoreCase("arena_kd")) {
                    int deaths = playerData.getArenaDeaths();
                    if (deaths == 0)
                        return String.format("%.2f", (double) playerData.getArenaKills());
                    return String.format("%.2f", (double) playerData.getArenaKills() / deaths);
                }

                if (params.equalsIgnoreCase("killstreak")) {
                    return String.valueOf(playerData.getCurrentKillstreak());
                }

                if (params.equalsIgnoreCase("best_killstreak")) {
                    return String.valueOf(playerData.getBestKillstreak());
                }
            }

            // Current election participation
            if (params.equalsIgnoreCase("has_voted")) {
                return plugin.getElectionManager().getElection().hasVoted(player.getUniqueId()) ? "Yes" : "No";
            }

            if (params.equalsIgnoreCase("is_candidate")) {
                return plugin.getElectionManager().getElection().getCandidate(player.getUniqueId()) != null ? "Yes"
                        : "No";
            }

            // Candidate-specific data
            var candidate = plugin.getElectionManager().getElection().getCandidate(player.getUniqueId());
            if (candidate != null) {
                if (params.equalsIgnoreCase("candidate_votes")) {
                    return String.format("%.1f",
                            plugin.getElectionManager().getElection().getCandidateVotes(player.getUniqueId()));
                }

                if (params.equalsIgnoreCase("candidate_endorsements")) {
                    return String.valueOf(candidate.getEndorsementCount());
                }

                if (params.equalsIgnoreCase("candidate_slogan")) {
                    return candidate.getSlogan() != null ? candidate.getSlogan() : "None";
                }
            }
        }

        return null;
    }

    private String getCabinetMemberName(CabinetPosition position) {
        var member = plugin.getGovernmentManager().getGovernment().getCabinetMemberObject(position);
        return member != null ? member.getName() : "None";
    }

    private String formatTime(long millis) {
        if (millis <= 0)
            return "0s";
        long seconds = millis / 1000;
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = (seconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days).append("d ");
        if (hours > 0)
            sb.append(hours).append("h ");
        if (minutes > 0)
            sb.append(minutes).append("m");
        if (sb.length() == 0)
            return seconds + "s";
        return sb.toString().trim();
    }
}
