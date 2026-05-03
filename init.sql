-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(255),
    rating DECIMAL(3,1),
    release_date DATE,
    embedding TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_genre ON movies(genre);
CREATE INDEX idx_year ON movies(year);
CREATE INDEX idx_rating ON movies(rating);
CREATE INDEX idx_director ON movies(director);
CREATE INDEX idx_created_at ON movies(created_at);

-- Insert sample movies (250 records)
INSERT INTO movies (title, description, genre, year, director, rating, release_date, created_at, updated_at) VALUES
('Timekeeper', 'A dark science fiction film about time travel. This thought-provoking story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Science Fiction', 2023, 'Christopher Nolan', 8.7, '2023-03-15', NOW(), NOW()),
('Silent Echoes', 'An intimate drama exploring family relationships. This heartwarming story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Drama', 2022, 'Ava DuVernay', 8.1, '2022-06-20', NOW(), NOW()),
('The Last Station', 'A gripping thriller about survival against odds. This intense story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Thriller', 2021, 'Denis Villeneuve', 8.4, '2021-11-10', NOW(), NOW()),
('Neon Memories', 'An epic action film with stunning cinematography. This dark story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Action', 2024, 'James Cameron', 8.9, '2024-01-05', NOW(), NOW()),
('Parallax', 'A psychological thriller about identity and reality. This mysterious story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Thriller', 2023, 'David Lynch', 8.3, '2023-09-12', NOW(), NOW()),
('The Infinite Loop', 'A mind-bending science fiction adventure about alternate realities. This thought-provoking story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Science Fiction', 2022, 'Christopher Nolan', 8.6, '2022-12-01', NOW(), NOW()),
('Shattered Mirror', 'A dark comedy about self-discovery. This quirky story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Comedy', 2021, 'Greta Gerwig', 7.9, '2021-07-22', NOW(), NOW()),
('Quantum Leap', 'An epic space exploration saga. This breathtaking story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Science Fiction', 2023, 'Denis Villeneuve', 8.8, '2023-05-18', NOW(), NOW()),
('The Forgotten Path', 'A haunting drama about redemption. This atmospheric story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Drama', 2022, 'Ari Aster', 8.2, '2022-09-08', NOW(), NOW()),
('Crimson Tide', 'An intense crime thriller about betrayal. This poignant story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Thriller', 2024, 'Martin Scorsese', 8.5, '2024-03-20', NOW(), NOW()),
('The Silent Void', 'A psychological thriller about isolation. This dark story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Thriller', 2023, 'David Lynch', 8.0, '2023-10-30', NOW(), NOW()),
('Midnight Protocol', 'A thrilling action film about corporate espionage. This intense story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Action', 2021, 'Christopher Nolan', 8.3, '2021-08-14', NOW(), NOW()),
('Echoes of Tomorrow', 'A science fiction epic about humanity''s future. This ambitious story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Science Fiction', 2022, 'Denis Villeneuve', 8.7, '2022-11-02', NOW(), NOW()),
('The Clockwork Heart', 'A romantic drama with supernatural elements. This heartwarming story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Romance', 2023, 'Damien Chazelle', 8.4, '2023-02-14', NOW(), NOW()),
('Nebula Rising', 'An epic adventure through space exploration. This breathtaking story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Adventure', 2024, 'James Cameron', 8.6, '2024-02-10', NOW(), NOW()),
('The Sanctuary', 'A mysterious thriller about hidden truths. This gripping story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Mystery', 2022, 'Bong Joon-ho', 8.2, '2022-07-24', NOW(), NOW()),
('Through the Veil', 'A surreal horror film about psychological breakdown. This haunting story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Horror', 2021, 'Ari Aster', 8.1, '2021-10-31', NOW(), NOW()),
('The Labyrinth', 'A complex drama about navigating life''s challenges. This ambitious story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Drama', 2023, 'Yorgos Lanthimos', 7.8, '2023-04-19', NOW(), NOW()),
('Cascading Shadows', 'A dark action thriller with great cinematography. This intense story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Action', 2024, 'Ryan Coogler', 8.5, '2024-05-15', NOW(), NOW()),
('The Convergence', 'A science fiction thriller about technological advancement. This thought-provoking story explores themes of human nature, resilience, and change. Winner of multiple international awards, this film captivates audiences with its compelling narrative and stunning cinematography. A must-watch masterpiece.', 'Science Fiction', 2022, 'Christopher Nolan', 8.3, '2022-08-30', NOW(), NOW());

-- Additional 230 records with diverse descriptions per genre
INSERT INTO movies (title, description, genre, year, director, rating, release_date)
SELECT
    'Movie ' || i::text,
    CASE (i % 18)
        WHEN 0 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'High-octane action sequences with explosions and car chases through major cities. Fast-paced action adventure filled with intense fight choreography and breathtaking action stunts.'
            WHEN 1 THEN 'Intense action film featuring hand-to-hand combat and daring heists. Adrenaline-pumping action scenes with skilled martial artists and explosive action sequences.'
            WHEN 2 THEN 'Action-packed thriller with chases and gunfights. Relentless action pacing featuring tough action heroes overcoming action obstacles and adversaries.'
            WHEN 3 THEN 'Dynamic action spectacle with superhero powers and large-scale action destruction. Action-driven narrative with extraordinary action capabilities and action heroism.'
            ELSE 'Raw action combat featuring special forces and tactical action missions. Gritty action sequences showcasing military action precision and action expertise.'
        END
        WHEN 1 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Side-splitting comedy with witty dialogue and hilarious situations. Laugh-out-loud funny humor featuring eccentric characters in absurd comedy circumstances.'
            WHEN 1 THEN 'Hilarious comedy rooted in character quirks and unexpected situations. Comedic comedy moments driven by funny character interactions and comedy timing.'
            WHEN 2 THEN 'Lighthearted comedy exploring romantic misadventures and comedic mishaps. Comedy filled with slapstick comedy humor and witty comedy exchanges.'
            WHEN 3 THEN 'Dark comedy blending humor with unconventional comedy themes. Subversive comedy narrative with satirical comedy observations and comedy irony.'
            ELSE 'Absurdist comedy featuring outlandish characters in surreal comedy scenarios. Comedy built on unexpected comedy situations and clever comedy wordplay.'
        END
        WHEN 2 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Emotionally powerful drama about family bonds and personal growth. Deep dramatic character development exploring profound human connections and emotional life lessons.'
            WHEN 1 THEN 'Intimate drama depicting internal struggles and personal transformation. Raw emotional drama exploring relationships, sacrifice, and dramatic personal journeys.'
            WHEN 2 THEN 'Character-driven drama examining moral dilemmas and ethical challenges. Serious dramatic narrative with complex character dynamics and dramatic consequences.'
            WHEN 3 THEN 'Poignant drama about loss, hope, and human resilience. Moving dramatic story showing emotional growth through difficult drama circumstances.'
            ELSE 'Multifaceted drama exploring societal issues through personal stories. Ambitious dramatic ensemble cast in interconnected dramatic narratives.'
        END
        WHEN 3 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Terrifying horror with jump scares and psychological dread. Supernatural horror beings haunting an isolated location with scary atmospheric tension throughout.'
            WHEN 1 THEN 'Visceral horror featuring grotesque creatures and disturbing imagery. Intense horror experience with graphic horror sequences and horrifying horror monsters.'
            WHEN 2 THEN 'Psychological horror exploring inner demons and mental deterioration. Unsettling horror atmosphere with horror themes of paranoia and horrific revelations.'
            WHEN 3 THEN 'Occult horror involving dark rituals and demonic entities. Gothic horror setting with terrifying horror incidents and supernatural horror forces.'
            ELSE 'Slasher horror with a relentless killer and survival horror elements. Brutal horror featuring horror victims and horror suspense leading to horrifying climax.'
        END
        WHEN 4 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Romantic love story with chemistry between the leads. Emotional romantic journey of two people discovering passion and romance commitment to each other.'
            WHEN 1 THEN 'Sweeping romance across different time periods and locations. Epic romantic narrative with passionate romance scenes and romantic destiny.'
            WHEN 2 THEN 'Tender romance about rediscovering love later in life. Romantic story exploring romantic vulnerability and emotional romance connections.'
            WHEN 3 THEN 'Tumultuous romance filled with conflicts and reconciliations. Complex romantic relationship with romantic tensions and romantic growth.'
            ELSE 'Cross-cultural romance bridging different worlds and backgrounds. Unconventional romantic bond overcoming romantic obstacles and romantic prejudice.'
        END
        WHEN 5 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Mind-bending science fiction exploring alternate realities and time travel. Futuristic science fiction technology and complex narrative with thought-provoking sci-fi concepts.'
            WHEN 1 THEN 'Dystopian science fiction depicting oppressive authoritarian regimes. Dark science fiction world with science fiction resistance and sci-fi rebellion.'
            WHEN 2 THEN 'Space opera featuring intergalactic science fiction warfare and alien civilizations. Epic science fiction saga spanning sci-fi galaxies and sci-fi empires.'
            WHEN 3 THEN 'Cyberpunk science fiction in virtual reality and digital worlds. High-tech science fiction setting with sci-fi hacking and science fiction corporate intrigue.'
            ELSE 'Post-apocalyptic science fiction after civilization collapse. Survival science fiction narrative in desolate sci-fi wastelands and science fiction ruins.'
        END
        WHEN 6 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Edge-of-your-seat thriller with unexpected plot twists. Suspenseful thriller narrative featuring a protagonist in mortal danger and thriller conspiracy.'
            WHEN 1 THEN 'Psychological thriller exploring mental manipulation and deception. Tense thriller with unreliable thriller narrators and psychological thriller twists.'
            WHEN 2 THEN 'Political thriller involving high-stakes espionage and thriller betrayal. Thriller featuring corruption, thriller secrets, and dangerous thriller powers.'
            WHEN 3 THEN 'Crime thriller following detectives pursuing thriller serial killers. Fast-paced thriller with thriller cat-and-mouse games and thriller justice.'
            ELSE 'Romantic thriller mixing passion with thriller danger. Thriller featuring obsessive thriller relationships and dangerous thriller attractions.'
        END
        WHEN 7 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Colorful animated adventure for families. Vibrant animation style with endearing animated characters and heartwarming messages about friendship and animation magic.'
            WHEN 1 THEN 'Whimsical animated comedy with slapstick animation humor. Playful animation featuring silly animated characters in hilarious animation situations.'
            WHEN 2 THEN 'Epic animated fantasy with animation magic and mythical animation creatures. Grand animation adventure with heroic animation quests and animation wonder.'
            WHEN 3 THEN 'Touching animated drama about animation friendship and animation belonging. Emotional animation story with deep animated character relationships and animation growth.'
            ELSE 'Adventure animation following animation heroes on animated quests. Exciting animation action with animation danger and animation triumph.'
        END
        WHEN 8 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Epic adventure quest across exotic lands with treasure hunting. Adventure packed with discovery, adventure exploration, and dangerous encounters with adventure exotic creatures.'
            WHEN 1 THEN 'Swashbuckling adventure featuring pirates and adventure on the high seas. Nautical adventure with adventure storms and treasure adventure hunts.'
            WHEN 2 THEN 'Jungle adventure exploring lost civilizations and adventure ruins. Adventure filled with adventure dangers, native adventure tribes, and adventure mysteries.'
            WHEN 3 THEN 'Mountain adventure featuring mountaineers overcoming adventure elements. Survival adventure in harsh adventure terrain with adventure endurance tests.'
            ELSE 'Desert adventure seeking adventure artifacts in ancient adventure tombs. Adventure journey through adventure sandstorms with adventure discoveries.'
        END
        WHEN 9 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Period drama set in a significant historical era. Authentic historical costumes and scenery depicting real historical events with character-driven historical narrative.'
            WHEN 1 THEN 'Victorian historical drama exploring historical class divisions. Elegant historical setting with historical manners and historical class struggle.'
            WHEN 2 THEN 'Revolutionary historical epic spanning historical upheaval and social historical change. Epic historical narrative with historical conflicts and historical transformation.'
            WHEN 3 THEN 'Royal historical saga featuring historical monarchs and historical court intrigue. Lavish historical production with historical palaces and historical dynasties.'
            ELSE 'War-torn historical drama during historical conflict periods. Tragic historical story set amid historical battles and historical suffering.'
        END
        WHEN 10 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Crime noir involving detectives investigating a murder mystery. Dark atmosphere with moral ambiguity and complex crime criminal underworld investigation.'
            WHEN 1 THEN 'Organized crime saga exploring crime family dynamics and crime loyalty. Brutal crime story with crime violence and crime betrayal within crime organizations.'
            WHEN 2 THEN 'Heist crime thriller planning elaborate crime capers and crime escapes. Intelligent crime narrative with crime strategy and crime execution.'
            WHEN 3 THEN 'Corruption crime drama exposing crime systemic crime problems. Gritty crime reality depicting crime violence and crime desperation.'
            ELSE 'Mob crime story following crime family rise and crime fall. Dark crime narrative with crime power struggles and crime consequences.'
        END
        WHEN 11 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Fantasy world with magic systems and mythical creatures. Epic fantasy battles between good and evil in richly detailed fantasy magical kingdoms.'
            WHEN 1 THEN 'Dark fantasy featuring morally ambiguous fantasy characters and fantasy corruption. Grim fantasy setting with fantasy darkness and fantasy moral complexity.'
            WHEN 2 THEN 'High fantasy epic with fantasy quests and fantasy prophecies. Grand fantasy saga spanning fantasy continents with fantasy legends.'
            WHEN 3 THEN 'Urban fantasy mixing fantasy magic into modern fantasy settings. Contemporary fantasy world with hidden fantasy societies and fantasy powers.'
            ELSE 'Sword-and-sorcery fantasy adventure with fantasy warriors and fantasy dragons. Action-packed fantasy featuring fantasy magic duels and fantasy monsters.'
        END
        WHEN 12 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Mystery puzzle box with clues leading to shocking revelation. Detective mystery work uncovering secrets and lies in small mystery communities.'
            WHEN 1 THEN 'Locked-room mystery with mystery suspects and mystery motives. Intricate mystery featuring mystery puzzles and mystery denouements.'
            WHEN 2 THEN 'Supernatural mystery exploring mystery phenomena and mystery unexplained events. Eerie mystery atmosphere with mystery hauntings and mystery phenomena.'
            WHEN 3 THEN 'Crime mystery following mystery investigation and mystery apprehension. Procedural mystery detailing mystery evidence and mystery interrogation.'
            ELSE 'Conspiracy mystery uncovering mystery cover-ups and mystery conspiracies. Thrilling mystery revealing mystery corruption and mystery truth.'
        END
        WHEN 13 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Documentary exploring real-world issues and events. Educational documentary content presenting factual information about important social or environmental topics.'
            WHEN 1 THEN 'Nature documentary featuring wildlife and documentary ecosystems. Breathtaking documentary visuals of documentary animals and documentary landscapes.'
            WHEN 2 THEN 'Biography documentary about influential documentary figures and documentary lives. Intimate documentary portrait of documentary achievements and documentary struggles.'
            WHEN 3 THEN 'Social documentary examining documentary injustices and documentary movements. Powerful documentary revealing documentary truths and documentary perspectives.'
            ELSE 'Historical documentary chronicling documentary events and documentary eras. Comprehensive documentary account of documentary significance and documentary impact.'
        END
        WHEN 14 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'War film depicting brutal combat and soldier camaraderie. Intense war battle sequences showing the human cost of conflict and military heroism.'
            WHEN 1 THEN 'WWI war depicting trench war warfare and war devastation. Gritty war portrayal of war suffering and war sacrifice.'
            WHEN 2 THEN 'WWII war epic spanning war continents and war theaters. Massive war production with war heroism and war tragedy.'
            WHEN 3 THEN 'Cold War political war thriller with war espionage and war tensions. Suspenseful war narrative with war paranoia and war brinksmanship.'
            ELSE 'Civil war depicting war brother-against-brother war conflict. Tragic war story exploring war families and war divisions.'
        END
        WHEN 15 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Western featuring gunslingers and frontier justice. Old West setting with outlaws, sheriffs, and showdowns in dusty western towns.'
            WHEN 1 THEN 'Revisionist western questioning western mythology and western violence. Modern western perspective on western history and western morality.'
            WHEN 2 THEN 'Spaghetti western with western standoffs and western vengeance. Stylized western featuring western bandits and western showdowns.'
            WHEN 3 THEN 'Frontier western about western settlers and western civilization. Pioneer western depicting western expansion and western hardship.'
            ELSE 'Neo-western blending western traditions with modern western sensibilities. Contemporary western exploring western codes and western change.'
        END
        WHEN 16 THEN CASE ((i / 18) % 5)
            WHEN 0 THEN 'Musical with elaborate dance numbers and original songs. Spectacular musical performances featuring talented singers and choreography and musical talent.'
            WHEN 1 THEN 'Jazz musical celebrating musical innovation and musical improvisation. Dynamic musical featuring musical musicians and musical performances.'
            WHEN 2 THEN 'Broadway musical with theatrical musical production and musical grandeur. Spectacular musical spectacle with musical ensemble and musical numbers.'
            WHEN 3 THEN 'Period musical set in musical historical settings and musical eras. Romantic musical featuring musical romance and musical period songs.'
            ELSE 'Animated musical combining animation and musical entertainment. Vibrant musical with musical animation and musical storytelling.'
        END
        ELSE CASE ((i / 18) % 5)
            WHEN 0 THEN 'Noir detective story with shadowy cinematography and moral corruption. Femme fatale and corrupt cops in rain-soaked noir city streets and darkness.'
            WHEN 1 THEN 'Hard-boiled noir featuring noir cynicism and noir violence. Gritty noir with noir antiheroes and noir moral ambiguity.'
            WHEN 2 THEN 'Neo-noir updating noir themes in contemporary noir settings. Modern noir with noir style and noir sensibilities.'
            WHEN 3 THEN 'Psychological noir exploring noir paranoia and noir madness. Noir atmosphere with noir obsession and noir decline.'
            ELSE 'Crime noir depicting noir crime syndicates and noir underworld. Murky noir with noir corruption and noir desperation.'
        END
    END,
    CASE (i % 18)
        WHEN 0 THEN 'Action'
        WHEN 1 THEN 'Comedy'
        WHEN 2 THEN 'Drama'
        WHEN 3 THEN 'Horror'
        WHEN 4 THEN 'Romance'
        WHEN 5 THEN 'Science Fiction'
        WHEN 6 THEN 'Thriller'
        WHEN 7 THEN 'Animation'
        WHEN 8 THEN 'Adventure'
        WHEN 9 THEN 'Historical'
        WHEN 10 THEN 'Crime'
        WHEN 11 THEN 'Fantasy'
        WHEN 12 THEN 'Mystery'
        WHEN 13 THEN 'Documentary'
        WHEN 14 THEN 'War'
        WHEN 15 THEN 'Western'
        WHEN 16 THEN 'Musical'
        ELSE 'Noir'
    END,
    2010 + (i % 15),
    CASE (i % 25)
        WHEN 0 THEN 'Christopher Nolan'
        WHEN 1 THEN 'Steven Spielberg'
        WHEN 2 THEN 'Martin Scorsese'
        WHEN 3 THEN 'Quentin Tarantino'
        WHEN 4 THEN 'Denis Villeneuve'
        WHEN 5 THEN 'Greta Gerwig'
        WHEN 6 THEN 'James Cameron'
        WHEN 7 THEN 'Cary Joji Fukunaga'
        WHEN 8 THEN 'David Lynch'
        WHEN 9 THEN 'Bong Joon-ho'
        WHEN 10 THEN 'Ari Aster'
        WHEN 11 THEN 'Damien Chazelle'
        WHEN 12 THEN 'Ryan Coogler'
        WHEN 13 THEN 'Destin Daniel Cretton'
        WHEN 14 THEN 'Ava DuVernay'
        WHEN 15 THEN 'Werner Herzog'
        WHEN 16 THEN 'Paul Thomas Anderson'
        WHEN 17 THEN 'Pedro Almodóvar'
        WHEN 18 THEN 'Hayao Miyazaki'
        WHEN 19 THEN 'Akira Kurosawa'
        WHEN 20 THEN 'Ingmar Bergman'
        WHEN 21 THEN 'Federico Fellini'
        WHEN 22 THEN 'Wes Anderson'
        WHEN 23 THEN 'Sam Esmail'
        ELSE 'Yorgos Lanthimos'
    END,
    ROUND((4.5 + (i % 50) * 0.1)::numeric, 1),
    CURRENT_DATE - INTERVAL '1 day' * (i % 3650)
FROM generate_series(21, 250) AS i;
