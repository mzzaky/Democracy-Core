import os

filepath = r'c:\Users\mohza\IdeaProjects\Democracy-Core-Plugin\src\main\java\id\democracycore\gui\GUIListener.java'
with open(filepath, 'r', encoding='utf-8') as f:
    text = f.read()

replacements = [
    ('<yellow>Gunakan: <white>/dc rate <1-5> <gray>to give rating for president', '<yellow>Use: <white>/dc rate <1-5> <gray>to give rating for president'),
    ('<yellow>Gunakan: <white>/dc treasury donate <value>', '<yellow>Use: <white>/dc treasury donate <value>'),
    ('<yellow>Gunakan: <white>/dc endorse <candidate_name>', '<yellow>Use: <white>/dc endorse <candidate_name>'),
    ('PlasyerStatsGUI.', 'PlayerStatsGUI.'),
    ('String candidateName = title.replace(VotingGUI.CANDIDATE_GUI_TITLE, "");', 'title.replace(VotingGUI.CANDIDATE_GUI_TITLE, ""); // Extracted for compatibility if needed'),
    ('public void onInventoryClick(InventoryClickEvent event) {', '@SuppressWarnings("deprecation")\n    public void onInventoryClick(InventoryClickEvent event) {'),
    ('public void onInventoryDrag(InventoryDragEvent event) {', '@SuppressWarnings("deprecation")\n    public void onInventoryDrag(InventoryDragEvent event) {'),
    ('if (!petition.getPhase().equals(RecallPetition.RecallPhase.SIGNING))', 'if (petition == null || !petition.getPhase().equals(RecallPetition.RecallPhase.SIGNING))'),
    ('if (!petition.getPhase().equals(RecallPetition.RecallPhase.VOTING))', 'if (petition == null || !petition.getPhase().equals(RecallPetition.RecallPhase.VOTING))'),
    ('MessageUtils.send(player, "<yellow>Gunakan: <white>/dc rate <1-5> <gray>untuk memberi rating presiden");', 'MessageUtils.send(player, "<yellow>Use: <white>/dc rate <1-5> <gray>to rate the president");'),
    ('<red>Petisi recall sedang AKTIF!', '<red>Recall petition is ACTIVE!'),
    ('<gray>Fase: <white>', '<gray>Phase: <white>'),
    ('<gray>Tanda tangan: <white>', '<gray>Signatures: <white>'),
    ('<gray>Tidak ada petisi recall aktif.', '<gray>No active recall petition.'),
    ('DAFTAR COMMAND', 'COMMAND LIST'),
    ('- Buka menu utama', '- Open main menu'),
    ('- Info pemerintahan', '- Government info'),
    ('- Bantuan command', '- Command help'),
    ('Pemilu:', 'Election:'),
    ('- Status pemilu', '- Election status'),
    ('- Daftar kandidat', '- Candidate list'),
    ('- Vote kandidat', '- Vote for a candidate'),
    ('- Endorse kandidat', '- Endorse a candidate'),
    ('Lainnya:', 'Others:'),
    ('- Info treasury', '- Treasury info'),
    ('- Rate presiden', '- Rate president'),
    ('- Statistik', '- Statistics'),
    ('- Sejarah presiden', '- History of presidents'),
    ('Gunakan:', 'Use:'),
    ('SISTEM RECALL', 'RECALL SYSTEM'),
    ('- Mulai petisi (50k deposit)', '- Start petition (50k deposit)'),
    ('- Tanda tangani petisi', '- Sign petition'),
    ('- Vote recall', '- Vote recall'),
    ('- Lihat status', '- View status')
]

for old, new in replacements:
    text = text.replace(old, new)


with open(filepath, 'w', encoding='utf-8') as f:
    f.write(text)

print('Translation applied successfully!')
