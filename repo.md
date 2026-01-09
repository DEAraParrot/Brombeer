# Brombeer Repository Overview

**Project Type**: Java Turn-Based Empire Management Game Engine  
**Latest Update**: January 9, 2026  
**Language**: Java  

---

## Quick Summary

Brombeer is a turn-based empire management game engine where players control rival factions (Dwarves, Humans, Ogres) competing to grow, build, research, and wage war. The game processes weekly changes automatically and maintains persistent game state through a save/load system.

---

## Directory Structure

```
Brombeer/
├── src/
│   ├── core/                      # Core game mechanics and systems
│   │   ├── Faction.java           # Abstract base faction class
│   │   ├── Army.java              # Military unit management
│   │   ├── Building.java          # Building instances
│   │   ├── BuildingDefinition.java # Building templates
│   │   ├── Resources.java         # Resource tracking (Food, Wood, Stone)
│   │   ├── Research.java          # Research progress
│   │   ├── ResearchEngine.java    # Research probability calculations
│   │   ├── ResearchResult.java    # Research outcome enum
│   │   ├── FactionRegistry.java   # Faction management & inter-faction distances
│   │   ├── SaveManager.java       # Save/load persistence system
│   │   ├── FactionStateFormatter.java # File I/O formatting
│   │   ├── WeeklyChangeProcessor.java # Weekly action processing
│   │   ├── WeeklyChangesLoader.java   # File detection and loading
│   │   └── ActionValidator.java   # Action validation and constraints
│   ├── factions/                  # Faction implementations
│   │   ├── Dwarfs.java           # Faction: Efficient food consumption (33%)
│   │   ├── Humans.java           # Faction: Faster population growth (33%)
│   │   └── Ogres.java            # Faction: Military strength bonus (2×)
│   ├── game/                      # Game entry point
│   │   ├── GameMain.java         # Main game loop and CLI interface
│   │   ├── ACTIONS_REFERENCE.txt # Player command reference
│   │   └── SAVE_SYSTEM_GUIDE.txt # Save system documentation
│   └── test/
│       └── BuildingPersistenceTest.java # Building system tests
├── data/                          # Initial faction configurations
│   ├── dwarfs.properties
│   ├── humans.properties
│   └── ogres.properties
├── saved/                         # Current game state saves
│   └── FactionName.week_N         # Weekly faction save files
├── backups/                       # Historical game states
│   └── backup.FactionName.week_N  # Archived saves
├── weeklychanges/                 # Weekly player action files
│   └── weekN.week                 # Weekly action instructions
├── out/                           # Compiled Java output
├── .zencoder/                     # Zencoder IDE configuration
├── Brombeer.iml                   # IntelliJ project configuration
├── SYSTEM_OVERVIEW.md             # Detailed system documentation
└── repo.md                        # This file
```

---

## Core Systems

### 1. **Faction System** (`core/Faction.java`)
Abstract base class for all playable factions with the following attributes:
- **Population**: Grows weekly (25% default, 33% for Humans)
- **Action Points**: max(3, population ÷ 1000) per week
- **Resources**: Food, Wood, Stone (expandable)
- **Armies**: Military forces with population and combat might
- **Buildings**: Structures producing/consuming resources
- **Research**: Progress tracking in various fields

**Faction Specialties**:
- **Dwarfs**: 33% food consumption (vs 50% standard)
- **Humans**: 33% population growth (vs 25% standard)
- **Ogres**: 2× military strength multiplier

### 2. **Resource System** (`core/Resources.java`)
Manages core resources:
- **Food**: Consumed by population, produced by Farms
- **Wood**: Building material, produced by Lumbermills
- **Stone**: Building material, produced by Quarries

Resources are expandable and tracked automatically through building production/consumption.

### 3. **Military System** (`core/Army.java`)
Manages military forces with:
- **States**: DEFENDING, ATTACKING, RETREATING, IDLE
- **Might Calculation**: Population + Modifiers (affected by faction bonuses)
- **Carrying Capacity**: Population ÷ 2
- **Travel Distance**: 2-3 weeks between factions
- **Combat**: Higher might wins; defeated armies lose 50% population

### 4. **Building System** (`core/Building.java`, `core/BuildingDefinition.java`)
Production infrastructure with:
- **Single Construction Queue**: One building at a time
- **Multi-week Construction**: Takes 2-4 weeks depending on type
- **Automatic Upkeep**: Resource consumption each week
- **Production**: Automatic resource generation
- **Recovery**: 15% resources refunded on demolition
- **Failure Condition**: Becomes dormant if no resources for 3+ weeks

**Available Buildings**:
- **Farm** (2 weeks, -5 food/week): +100 food/week
- **Lumbermill** (3 weeks, -10 food/week): +80 wood/week
- **Quarry** (4 weeks, -10 food/week): +60 stone/week

### 5. **Research System** (`core/Research.java`, `core/ResearchEngine.java`)
Probability-based research with outcomes:
- **Success Chance**: Base 50% + modifiers from invested Action Points
- **Breakthrough Chance**: Base 5% (on successful research)
- **Formula**: Chance = Base + (Additive × Multiplicative)
- **Outcomes**: Failure, Discovery, Breakthrough

### 6. **Save/Load System** (`core/SaveManager.java`)
Persistent game state management:
- **Format**: Human-readable text files with sections
- **Current Saves**: `saved/FactionName.week_N`
- **Archived Saves**: `backups/backup.FactionName.week_N`
- **Config Files**: `data/faction_name.properties`
- **Auto-archiving**: Old saves moved to backups when new saves created

### 7. **Weekly Processing** (`core/WeeklyChangeProcessor.java`)
Processes game weeks in this order:
1. Load weekly changes from `weeklychanges/` folder
2. Parse faction actions from files
3. Validate all actions
4. Apply faction changes (population growth, resource production, etc.)
5. Calculate army movements and battles
6. Save new week state
7. Archive previous week to backups

### 8. **Action Validation** (`core/ActionValidator.java`)
Ensures game rules compliance:
- Sufficient population for creating armies
- Adequate resources for constructing buildings
- Army existence and viability for actions
- Building completion before demolition

---

## Game Commands

| Command | Effect |
|---------|--------|
| `weekend` | Process current week, save state, archive old saves |
| `newgame` | Delete all saves, reset to week 0 from config files |
| `status` | Display all faction statistics |
| `backups` | List available backup weeks for restoration |
| `restore` | Restore game from a specific historical week |
| `help` | Show command reference |
| `exit` | Close game (no auto-save) |

---

## Weekly Action File Format

**Location**: `weeklychanges/week1.week`, `weeklychanges/week2.week`, etc.

```
# Comments start with #
FACTION dwarfs
ARMY_CREATE MainForce, 1000
BUILDING_CONSTRUCT Farm
RESEARCH Mining, 2

FACTION humans
ARMY_CREATE LegionA, 1500
BUILDING_CONSTRUCT Lumbermill

FACTION ogres
ARMY_CREATE WarBand, 800
RESEARCH Warfare
```

**Supported Actions**:
- `FACTION FactionName` - Switch active faction
- `ARMY_CREATE ArmyName, Population` - Create military force
- `ARMY_ATTACK ArmyName, TargetFaction` - Begin attack
- `ARMY_PROTECT ArmyName, Target` - Set to defensive
- `ARMY_RETREAT ArmyName` - Retreat from combat
- `BUILDING_CONSTRUCT BuildingType` - Start building construction
- `BUILDING_DEMOLISH BuildingId` - Destroy building and recover resources
- `RESEARCH FieldName [InvestedAP]` - Research with optional AP investment

---

## Save File Format

**Location**: `saved/FactionName.week_N`

```
dwarfs
Population=5000
ActionPoints=5

Resources:
  food=2500
  wood=500
  stone=800

Armies:
  MainForce: 1000 soldiers, 1050 might, DEFENDING

Buildings:
  Farm_1 (Farm): Complete
  Farm_2 (Farm): 1 weeks remaining

Research:
  Mining: Discovery
  Warfare: Failure

Features:
  [Space for custom faction notes]
```

---

## Game Mechanics

### Population Growth
- **Default**: +25% per week
- **Humans**: +33% per week
- **Consumption**: (Faction Population + All Armies Population) ÷ 2 per week
- **Starvation**: If food < population at week start and end, population reduced to available food

### Action Points (AP)
- **Calculation**: max(3, population ÷ 1000) per week
- **Reset**: Full reset each week
- **Validation**: Cannot exceed available AP in actions

### Combat Mechanics
- **Travel Time**: Armies take 2-3 weeks to reach destination (depends on faction distance)
- **Resolution**: Higher might wins
- **Losses**: Defeated armies lose 50% population
- **Plunder**: Victorious army may capture opponent resources

### Distance Matrix (Travel Weeks)
| From | To | Weeks |
|------|-----|-------|
| Dwarfs | Humans | 2 |
| Dwarfs | Ogres | 3 |
| Humans | Ogres | 2 |

---

## Data Persistence Flow

```
Game Startup
  ├─ SaveManager.getCurrentWeek()
  │   └─ Scans saved/ for .week_N files
  ├─ If week > 0:
  │   └─ SaveManager.loadGameState()
  │       └─ Load from saved/FactionName.week_N
  └─ If week == 0:
      └─ faction.loadFactionData()
          └─ Load from data/faction_name.properties

Weekly Resolution
  ├─ User enters "weekend" command
  ├─ WeeklyChangeProcessor.apply()
  ├─ Faction.processWeek() [all calculations]
  ├─ SaveManager.saveGameState()
  │   ├─ archiveCurrentWeek()
  │   │   └─ Move saved/* to backups/backup.*
  │   └─ Create new saved/FactionName.week_N files
  └─ Ready for next week

Game Recovery
  ├─ User enters "restore" command
  ├─ SaveManager.listAvailableWeeks()
  │   └─ Scan backups/ directory
  ├─ User selects week
  ├─ SaveManager.restoreWeek()
  │   └─ Copy backup.* files to saved/
  └─ Continue gameplay from that week
```

---

## Key Classes and Responsibilities

| Class | Responsibility |
|-------|-----------------|
| `Faction.java` | Abstract base faction with core mechanics |
| `FactionRegistry.java` | Global faction management and distances |
| `Army.java` | Military force management and combat |
| `Building.java` | Individual building instances |
| `BuildingDefinition.java` | Building templates and definitions |
| `Resources.java` | Resource tracking and management |
| `Research.java` | Research progress and state |
| `ResearchEngine.java` | Research probability calculations |
| `SaveManager.java` | File I/O and persistence |
| `FactionStateFormatter.java` | Save file formatting and parsing |
| `WeeklyChangeProcessor.java` | Action processing and game state updates |
| `ActionValidator.java` | Action validation and constraint checking |
| `GameMain.java` | CLI game loop and user interface |

---

## Extensibility

### Adding New Resources
Edit `Resources.java` and add:
```java
resources.put("ResourceName", initialAmount);
// Add getter/setter methods
```

### Adding New Building Types
Edit `BuildingDefinition.java`:
```java
BuildingDefinition newBuilding = new BuildingDefinition("BuildingName", constructionWeeks);
newBuilding.setConstructionCost("wood", amount);
newBuilding.setProductionRate("resource", rate);
newBuilding.setUpkeepCost("food", cost);
BUILDINGS.put("BuildingName", newBuilding);
```

### Adding New Actions
Edit `WeeklyChangeProcessor.java`:
```java
case "ACTION_NAME":
    handleActionName(faction, parts);
    break;
```

### Adding Faction Specialties
Override methods in faction subclasses (`Dwarfs.java`, `Humans.java`, `Ogres.java`):
```java
@Override
protected void consumeFood() {
    // Custom food consumption logic
}
```

---

## Testing

### Test Files
- `src/test/BuildingPersistenceTest.java` - Building system persistence tests

### Manual Testing Checklist
- [ ] New game loads from config files
- [ ] Weekend resolution processes changes correctly
- [ ] Saves appear in `saved/` with correct naming
- [ ] Old saves move to `backups/` and are renamed
- [ ] `status` command displays correct values
- [ ] `restore` command recovers old saves
- [ ] `newgame` deletes saves but keeps backups
- [ ] Population growth applies correctly
- [ ] Food consumption prevents starvation
- [ ] Resources are properly tracked
- [ ] Buildings produce and consume resources
- [ ] Armies can be created and managed
- [ ] Research outcomes are randomized
- [ ] Action validation prevents invalid moves

---

## Future Enhancement Roadmap

1. **Combat Resolution**: Full tactical battle system with unit types
2. **Heroes/Lords**: Special units with unique abilities and progression
3. **Diplomacy System**: Alliances, trade agreements, treaties
4. **Random Events**: Events affecting factions (disasters, discoveries, etc.)
5. **Additional Factions**: More playable civilizations with unique mechanics
6. **Extended Resources**: Gold, mana, population subtypes
7. **Tech/Magic Trees**: Research unlocks new mechanics and buildings
8. **Map System**: Visual faction location representation
9. **Web Interface**: Browser-based game client
10. **Multiplayer Support**: Real-time or turn-based PvP gameplay

---

## Documentation Reference

- **SYSTEM_OVERVIEW.md**: Detailed system design and architecture
- **src/game/ACTIONS_REFERENCE.txt**: Player command reference
- **src/game/SAVE_SYSTEM_GUIDE.txt**: Save system detailed guide
- **repo.md**: This file (repository overview)

---

## Project Statistics

| Metric | Value |
|--------|-------|
| Total Source Files | 18 |
| Core Classes | 16 |
| Test Classes | 1 |
| Total Lines (estimated) | ~3000 |
| Language | Java |
| Build System | Java (IDE: IntelliJ IDEA) |

---

**Repository Last Updated**: January 9, 2026  
**Game Version**: 1.0 - Base System Complete
