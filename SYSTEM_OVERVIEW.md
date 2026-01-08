# Brombeer Game Engine - Complete System Overview

## Project Summary

A turn-based empire management game engine where players control factions (Dwarves, Humans, Ogres) that grow, build, research, and wage war. The game processes weekly changes loaded from files and calculates all faction state changes automatically.

---

## Directory Structure

```
Brombeer/
├── src/
│   ├── core/                    # Game logic and mechanics
│   │   ├── Faction.java         # Base faction class
│   │   ├── Army.java            # Military units
│   │   ├── Building.java        # Building instances
│   │   ├── BuildingDefinition.java  # Building types
│   │   ├── Resources.java       # Resource management
│   │   ├── Research.java        # Research tracking
│   │   ├── ResearchEngine.java  # Research probability
│   │   ├── ResearchResult.java  # Research outcome enum
│   │   ├── FactionRegistry.java # Faction management & distances
│   │   ├── SaveManager.java     # Save/load system
│   │   ├── FactionStateFormatter.java  # File I/O
│   │   ├── WeeklyChangeProcessor.java  # Weekly actions
│   │   ├── WeeklyChangesLoader.java    # File detection
│   │   └── ActionValidator.java # Action validation
│   ├── factions/                # Faction implementations
│   │   ├── Dwarfs.java
│   │   ├── Humans.java
│   │   └── Ogres.java
│   ├── game/                    # Game entry point
│   │   ├── GameMain.java
│   │   ├── ACTIONS_REFERENCE.txt  # Player reference
│   │   └── SAVE_SYSTEM_GUIDE.txt  # Save system docs
│   ├── backups/                 # Backup system
│   │   └── BackupManager.java
│   └── saved/                   # Current game saves
│       ├── dwarfs.week_N
│       ├── humans.week_N
│       └── ogres.week_N
├── data/                        # Initial faction configs
│   ├── dwarfs.properties
│   ├── humans.properties
│   └── ogres.properties
├── saved/                       # Current week files
├── backups/                     # Historical saves
├── weeklychanges/               # Weekly action files
│   └── week1.week
└── README files                 # Documentation
```

---

## Core Systems

### 1. **Faction System**
- **Base Class**: `Faction.java` (abstract)
- **Implementations**: Dwarfs, Humans, Ogres
- **Attributes**:
  - Population (grows 25%/week, Humans grow 33%)
  - Action Points (max(3, population/1000))
  - Resources (Food, Wood, Stone - expandable)
  - Armies (military forces with population and might)
  - Buildings (structures with production/upkeep)
  - Research (progress tracking with probabilities)

**Faction Specialties**:
- **Dwarfs**: 33% food consumption (vs 50% standard)
- **Humans**: 33% population growth (vs 25% standard)
- **Ogres**: 2× military strength multiplier

### 2. **Resource System**
- **Class**: `Resources.java`
- **Core Resources**: 
  - Food (consumed by population, produced by farms)
  - Wood (building material, produced by lumbermills)
  - Stone (building material, produced by quarries)
- **Expandable**: Easy to add new resources
- **Economy**: 
  - Players start with different amounts
  - Buildings produce/consume resources
  - Population survives only if food >= population

### 3. **Military System**
- **Class**: `Army.java`
- **States**: DEFENDING, ATTACKING, RETREATING, IDLE
- **Mechanics**:
  - Might = Population + Modifiers
  - Carrying capacity = Population / 2
  - Distance-based travel (2-3 weeks between factions)
  - Combat resolution (placeholder for full implementation)

**Actions**:
- `ARMY_CREATE ArmyName, Population`
- `ARMY_ATTACK ArmyName, TargetFaction`
- `ARMY_PROTECT ArmyName, Target`
- `ARMY_RETREAT ArmyName`

### 4. **Building System**
- **Classes**: `Building.java`, `BuildingDefinition.java`
- **Mechanics**:
  - One building construction at a time
  - Multi-week construction timer
  - Automatic upkeep and production
  - 15% resource recovery on demolition
  - Fails if dormant > 3 weeks

**Available Buildings**:
- **Farm** (2 weeks): +100 food/week, -5 food upkeep
- **Lumbermill** (3 weeks): +80 wood/week, -10 food upkeep
- **Quarry** (4 weeks): +60 stone/week, -10 food upkeep

**Actions**:
- `BUILDING_CONSTRUCT BuildingType`
- `BUILDING_DEMOLISH BuildingId`

### 5. **Research System**
- **Classes**: `Research.java`, `ResearchEngine.java`, `ResearchResult.enum`
- **Mechanics**:
  - Probability-based outcomes
  - Formula: `Chance = Base + (Additive × Multiplicative)`
  - Base success: 50%
  - Base breakthrough: 5% (on success)
  - Invested AP increases both chances

**Outcomes**: Failure, Discovery, Breakthrough

**Action**:
- `RESEARCH FieldName [InvestedAP]`

### 6. **Save/Load System**
- **Class**: `SaveManager.java`
- **Format**: Human-readable text files with sections
- **Automatic Archiving**:
  - Old saves moved to `backups/backup.Faction.week_N`
  - Current saves stay in `saved/Faction.week_N`
- **File Locations**:
  - Current: `saved/FactionName.week_N`
  - History: `backups/backup.FactionName.week_N`
  - Config: `data/faction_name.properties`

### 7. **Weekly Processing**
- **Class**: `WeeklyChangeProcessor.java`
- **Flow**:
  1. Load weekly changes from `weeklychanges/` folder
  2. Apply all faction actions
  3. Calculate faction changes (population, resources, armies)
  4. Save new week state
  5. Archive previous week

### 8. **Validation System**
- **Class**: `ActionValidator.java`
- **Validates**:
  - Sufficient population for armies
  - Resource availability for buildings
  - Army existence and viability
  - Building completion before demolition

---

## Game Commands

| Command | Effect |
|---------|--------|
| `weekend` | Process current week, save state, archive old saves |
| `newgame` | Delete all saves, reset to week 0 from config |
| `status` | Display all faction stats |
| `backups` | List available backup weeks |
| `restore` | Restore game from specific week |
| `help` | Show command reference |
| `exit` | Close game (no auto-save) |

---

## Weekly File Format

**File Location**: `weeklychanges/weekN.week`

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
  [Space for manual faction notes]
```

---

## Game Mechanics Summary

### Population
- **Growth**: +25% per week (Humans +33%)
- **Consumption**: (Population + ArmiesPopulation) / 2 per week
- **Starvation**: If food < population at week start AND end, population = food

### Action Points
- **Calculation**: max(3, population / 1000)
- **Reset**: Each week
- **Validation**: Cannot exceed available AP

### Combat (Placeholder)
- Armies reach target after N weeks travel
- Higher might wins
- Defeated armies lose 50% population
- Pursuing army might capture resources

### Distance Matrix
| From | To | Weeks |
|------|-----|-------|
| dwarfs | humans | 2 |
| dwarfs | ogres | 3 |
| humans | ogres | 2 |

---

## Extensibility

### Adding New Resources
Edit `Resources.java`:
```java
resources.put("name", 0);
// Add getter/setter methods
```

### Adding New Buildings
Edit `BuildingDefinition.java`:
```java
BuildingDefinition newBuilding = new BuildingDefinition("Name", weeks);
newBuilding.setConstructionCost("wood", amount);
// ... more setup
BUILDINGS.put("Name", newBuilding);
```

### Adding New Actions
Edit `WeeklyChangeProcessor.java`:
```java
case "ACTION_NAME":
    handleActionName(faction, parts);
    break;
```

### Adding Faction Specialties
Override methods in faction subclasses:
```java
@Override
protected void consumeFood() { /* custom logic */ }
```

---

## Data Persistence Flow

```
Startup
  ├─ SaveManager.getCurrentWeek()
  │   └─ Scans saved/ for .week_N files
  ├─ If week > 0:
  │   └─ SaveManager.loadGameState()
  └─ If week == 0:
      └─ faction.loadFactionData() [from data/]

Weekly Resolution
  ├─ User enters "weekend"
  ├─ WeeklyChangeProcessor.apply()
  ├─ Faction.processWeek() [all calculations]
  ├─ SaveManager.saveGameState()
  │   ├─ archiveCurrentWeek() [move to backups/]
  │   └─ Save new .week_N files
  └─ Ready for next week

Recovery
  ├─ User enters "restore"
  ├─ SaveManager.listAvailableWeeks() [from backups/]
  ├─ SaveManager.restoreWeek()
  │   └─ Copy backup.* → saved/
  └─ Continue from that week
```

---

## Testing Checklist

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

## Future Enhancements

1. **Combat Resolution**: Full battle system with tactics
2. **Heroes/Lords**: Special units with abilities
3. **Diplomacy**: Alliances, trade, treaties
4. **Events**: Random events affecting factions
5. **More Factions**: Additional playable civilizations
6. **More Resources**: Gold, mana, population subtypes
7. **Magic/Tech Trees**: Discovery unlock new mechanics
8. **Map System**: Visualization of faction locations
9. **Web Interface**: Browser-based game client
10. **Multiplayer**: Real-time or turn-based PvP

---

## File Size Reference

| Directory | Purpose |
|-----------|---------|
| `core/` | ~14 KB total (game logic) |
| `factions/` | ~1 KB total (faction definitions) |
| `game/` | ~6 KB total (UI/entry point) |
| `saved/` | Grows with gameplay (~1-5 KB/week) |
| `backups/` | Grows indefinitely (~1-5 KB/week) |

---

**Last Updated**: January 8, 2026
**Version**: 1.0 - Base System Complete
