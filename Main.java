import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Main {
    private static final List<Book> library = new ArrayList<>();
    private static final List<User> users = new ArrayList<>();
    private static final List<Request> requests = new ArrayList<>();
    private static final List<BorrowRecord> borrows = new ArrayList<>();
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();
    private static final Map<String, String> flipbooks = new HashMap<>();
    public static int BORROW_DAYS = 7;

    private static final Path DATA_DIR = Paths.get(System.getenv().getOrDefault("DATA_DIR", "/data"));
    private static final Path DATA_FILE = DATA_DIR.resolve("users.dat");
    private static final Path REQUESTS_FILE = DATA_DIR.resolve("requests.dat");
    private static final Path BORROWS_FILE = DATA_DIR.resolve("borrows.dat");

    public static void main(String[] args) throws Exception {
        // ── Books ───────────────────────────────────────────
        library.add(Book.load("Advanced Mathematics", "TIE", "T009", "Tie", "A-Level", "Form 5",
            "Advanced mathematics covers calculus, complex numbers, and advanced statistics.<br><br>Chapter 1: Differentiation<br>Chapter 2: Integration<br>Chapter 3: Complex Numbers<br>Chapter 4: Vectors<br>Chapter 5: Probability Distributions"));
        // -- Non-TIE books removed temporarily (Novels, Plays, Reference, Religion) --
        // library.add(Book.load("A River Between", "Ngugi wa Thiong'o", "N001", "Novel", "Secondary", "Novel",
        //     "The river was the soul of the land. ..."));
        // library.add(Book.load("Weep Not Child", "Ngugi wa Thiong'o", "N002", "Novel", "Secondary", "Novel",
        //     "Njoroge sat under the mugumo tree..."));
        // library.add(Book.load("The Lion and the Jewel", "Wole Soyinka", "P001", "Play", "Secondary", "Play",
        //     "The village of Ilujinle wakes to the sound of drums..."));
        // library.add(Book.load("Three Suitors One Husband", "Moliere", "P002", "Play", "Secondary", "Play",
        //     "A comedy of errors unfolds..."));
        // library.add(Book.load("Oxford English Dictionary", "Oxford Press", "R001", "Reference", "All", "All",
        //     "The definitive record of the English language..."));
        // library.add(Book.load("Encyclopedia of Science", "DK Publishing", "R002", "Reference", "All", "All",
        //     "A comprehensive guide to the world of science..."));
        // library.add(Book.load("The Holy Bible", "Various", "G001", "Religion", "All", "All",
        //     "The sacred scripture of Christianity..."));

        library.add(Book.load("Advanced Biology", "TIE", "T012", "Tie", "A-Level", "Form 5",
            "Advanced Biology Form 5 explores the principles of cytology, genetics, evolution, ecology, and physiology of living organisms. The book is designed to equip students with knowledge and skills necessary for advanced studies in biological sciences.<br><br>Topics: Cell Biology and Organisation, Genetics and Variation, Evolution Theories, Ecology and Ecosystems, Plant and Animal Physiology, Reproduction and Growth, Classification of Organisms."));
        library.add(Book.load("History", "TIE", "T013", "Tie", "A-Level", "Form 5",
            "History Form 5 covers African history from pre-colonial times through independence. Topics include the African continent before colonialism, the slave trade and its abolition, the European scramble and partition of Africa, colonial administration systems, African nationalism, and the struggle for independence. The book follows the 2023 advanced secondary curriculum.<br><br>Topics: Pre-Colonial African Societies, The Trans-Atlantic Slave Trade, The Scramble for Africa, Colonial Administration in Africa, African Nationalism and Independence Movements, Post-Independence Africa."));
        library.add(Book.load("Accountancy", "TIE", "T019", "Tie", "A-Level", "Form 5",
            "Advanced Accountancy Form 5 introduces the principles and practice of financial accounting and reporting. It covers the complete accounting cycle from source documents to final accounts, correction of errors, bank reconciliation, control accounts, depreciation, and accounting for partnerships.<br><br>Topics: Accounting Concepts and Conventions, The Accounting Cycle, Books of Original Entry, Ledger Posting and Trial Balance, Final Accounts of Sole Traders, Correction of Errors, Bank Reconciliation Statements, Control Accounts, Depreciation Accounting, Partnership Accounts, Introduction to Company Accounts."));
        library.add(Book.load("Kiswahili", "TIE", "T016", "Tie", "A-Level", "Form 5",
            "Kiswahili Kidato cha 5 (Kiwango cha Juu) kinajikita katika isimu ya Kiswahili, fasihi, na utamaduni. Mada muhimu ni pamoja na fonologia, mofolojia, sintaksia, semantiki, uandishi wa insha, tafiti za fasihi simulizi na andishi, na uchambuzi wa matini mbalimbali za kifasihi.<br><br>Topics: Fonologia na Mofolojia, Sintaksia na Muundo wa Sentensi, Semantiki na Pragmatiki, Historia ya Kiswahili na Maendeleo Yake, Fasihi Simulizi, Fasihi Andishi: Riwaya, Tamthilia, Ushairi, Uhakiki wa Fasihi, Uandishi wa Insha na Makala, Tafiti na Utafiti wa Kiswahili."));
        library.add(Book.load("Physical Geography", "TIE", "T014", "Tie", "A-Level", "Form 5",
            "Physical Geography for Advanced Secondary Schools Form Five covers the earth's structure, landforms, weather and climate, soils, vegetation, and environmental processes. The book follows the 2023 Geography syllabus.<br><br>Topics: Earth's Structure and Landforms, Weather and Climate, Soils and Vegetation, Map Reading and Interpretation, Environmental Conservation, Climatology, Geomorphology, Biogeography."));
        // library.add(Book.load("Practical Geography", "TIE", "T014p", "Tie", "A-Level", "Form 5",
        //     "Practical Geography Form 5 covers map reading, interpretation, and field work techniques. This supplementary guide provides hands-on exercises for mastering topographic maps, statistical diagrams, photograph interpretation, and research methodology in geography.<br><br>Topics: Map Reading and Interpretation, Statistical Methods and Diagrams, Photograph Interpretation, Field Research Techniques, Surveying and Sketching, Climate Data Analysis, Population Data Analysis, Environmental Impact Assessment."));
        library.add(Book.load("Computer Science", "TIE", "T022", "Tie", "A-Level", "Form 5",
            "Computer Science Form Five covers computer basics, data representation, problem solving, C++ programming, website development, and system development.<br><br>Topics: Computer Basics, Data Representation, Problem Solving, C++ Programming, Website Development, System Development."));

        library.add(Book.load("English", "TIE", "T023", "Tie", "A-Level", "Form 5",
            "English for Advanced Secondary Schools Form Five develops proficiency in English language skills. The book covers reading comprehension, writing, grammar, oral communication, and literary analysis following the 2023 English syllabus.<br><br>Topics: Reading and Comprehension, Writing Skills, Grammar and Usage, Oral Communication, Literary Analysis, Summary Writing, Note Making, Report Writing."));
        library.add(Book.load("Academic Communication", "TIE", "T024", "Tie", "A-Level", "Form 5",
            "Academic Communication for Advanced Secondary Schools Form Five builds essential academic communication skills. The textbook covers academic writing, research skills, presentations, and scholarly discourse following the 2023 syllabus.<br><br>Topics: Academic Writing, Research Methodology, Presentation Skills, Scholarly Discourse, Referencing and Citation, Academic Integrity."));
        library.add(Book.load("General and Inorganic Chemistry", "TIE", "T025", "Tie", "A-Level", "Form 5",
            "General and Inorganic Chemistry for Advanced Secondary Schools covers the fundamental principles of chemistry including atomic structure, chemical bonding, periodic trends, and inorganic reactions. The book follows the 2023 Chemistry syllabus.<br><br>Topics: Atomic Structure and Periodicity, Chemical Bonding, States of Matter, Chemical Kinetics, Chemical Equilibrium, Acids and Bases, Electrochemistry, Transition Elements."));
        library.add(Book.load("Organic Chemistry", "TIE", "T026", "Tie", "A-Level", "Form 5",
            "Organic Chemistry for Advanced Secondary Schools covers the structure, properties, and reactions of organic compounds. The textbook follows the 2023 Chemistry syllabus for Form V-VI.<br><br>Topics: Introduction to Organic Chemistry, Hydrocarbons, Alkyl Halides, Alcohols and Phenols, Aldehydes and Ketones, Carboxylic Acids and Derivatives, Amines, Polymers and Biomolecules."));
        library.add(Book.load("Basic Applied Mathematics", "TIE", "T027", "Tie", "A-Level", "Form 5",
            "Basic Applied Mathematics for Advanced Secondary Schools Form Five covers practical mathematical applications. The book follows the 2023 Basic Applied Mathematics syllabus.<br><br>Topics: Logic and Set Theory, Functions and Relations, Linear Programming, Matrices and Transformations, Probability and Statistics, Financial Mathematics, Calculus Applications."));
        library.add(Book.load("Literature in English", "TIE", "T028", "Tie", "A-Level", "Form 5",
            "Literature in English for Advanced Secondary Schools Form Five covers the analysis of prose, poetry, and drama. The book follows the 2023 Literature in English syllabus.<br><br>Topics: Introduction to Literary Analysis, Prose Fiction, Poetry, Drama, African Literature, Literary Criticism, Oral Literature, Essay Writing on Literary Works."));
        library.add(Book.load("Business Studies", "TIE", "T029", "Tie", "A-Level", "Form 5",
            "Business Studies for Advanced Secondary Schools Form Five covers the principles and practices of business management, entrepreneurship, and commerce. The book follows the 2023 Business Studies syllabus.<br><br>Topics: Business Environment, Entrepreneurship, Business Management, Marketing, Production, Business Finance, Accounting Basics, Business Ethics and Social Responsibility."));

        // ── New Form 5 Books (from TIE self-hosted) ──
        library.add(Book.load("Chinese", "TIE", "T030", "Tie", "A-Level", "Form 5",
            "Chinese for Advanced Secondary Schools Form Five covers basic Chinese language skills including speaking, listening, reading, and writing. The book follows the 2023 Chinese syllabus.<br><br>Topics: Introduction to Chinese Language, Chinese Pronunciation and Tones, Basic Grammar, Daily Conversations, Chinese Characters, Reading Comprehension, Chinese Culture and Customs."));
        library.add(Book.load("Divinity", "TIE", "T031", "Tie", "A-Level", "Form 5",
            "Divinity for Advanced Secondary Schools Form Five covers theological studies, biblical knowledge, and religious education. The book follows the 2023 Divinity syllabus.<br><br>Topics: Introduction to Divinity, Old Testament Studies, New Testament Studies, Christian Theology, African Traditional Religion, Comparative Religion, Church History, Christian Ethics."));
        library.add(Book.load("Elimu ya Dini ya Kiislamu", "TIE", "T032", "Tie", "A-Level", "Form 5",
            "Elimu ya Dini ya Kiislamu kwa Kidato cha Tano inajikita katika mafundisho ya Kiislamu, Qur'an, Hadithi, na maadili.<br><br>Topics: Tawheed, Tafsir, Hadith, Fiqh, Akhlaq, Historia ya Uislamu, Maadili ya Kiislamu."));
        library.add(Book.load("Fasihi ya Kiswahili", "TIE", "T033", "Tie", "A-Level", "Form 5",
            "Fasihi ya Kiswahili kwa Shule za Sekondari Kidato cha Tano inajikita katika uchambuzi wa fasihi simulizi na andishi.<br><br>Topics: Fasihi Simulizi, Fasihi Andishi, Uchambuzi wa Riwaya, Uchambuzi wa Tamthilia, Uchambuzi wa Ushairi, Uhaktiki wa Fasihi, Nadharia za Fasihi."));
        library.add(Book.load("Arabic", "TIE", "T034", "Tie", "A-Level", "Form 5",
            "Arabic for Advanced Secondary Schools Form Five covers Arabic language skills including grammar, reading, writing, and speaking. The book follows the 2023 Arabic syllabus.<br><br>Topics: Arabic Grammar (Nahw), Morphology (Sarf), Reading Comprehension, Writing Skills, Speaking and Listening, Arabic Literature, Islamic Texts."));
        library.add(Book.load("Food and Human Nutrition", "TIE", "T035", "Tie", "A-Level", "Form 5",
            "Food and Human Nutrition for Advanced Secondary Schools Form Five covers the study of nutrients, meal planning, food preparation, and nutritional health. The book follows the 2023 syllabus.<br><br>Topics: Introduction to Nutrition, Macronutrients and Micronutrients, Meal Planning and Management, Food Preparation and Preservation, Nutritional Disorders, Food Science and Technology."));
        library.add(Book.load("Theatre Arts", "TIE", "T036", "Tie", "A-Level", "Form 5",
            "Theatre Arts for Advanced Secondary Schools Form Five covers the principles of drama, performance, stagecraft, and theatrical production. The book follows the 2023 Theatre Arts syllabus.<br><br>Topics: Introduction to Theatre, Acting Techniques, Stage Design and Management, Directing, Playwriting, African Theatre, Performance Analysis."));
        library.add(Book.load("Music", "TIE", "T037", "Tie", "A-Level", "Form 5",
            "Music for Advanced Secondary Schools Form Five covers music theory, history, performance, and composition. The book follows the 2023 Music syllabus.<br><br>Topics: Music Theory and Notation, African Music, Western Music History, Instrumental Performance, Vocal Music, Music Composition, Aural Skills."));
        library.add(Book.load("Tourism", "TIE", "T038", "Tie", "A-Level", "Form 5",
            "Tourism for Advanced Secondary Schools Form Five covers the principles of tourism, travel management, hospitality, and sustainable tourism. The book follows the 2023 Tourism syllabus.<br><br>Topics: Introduction to Tourism, Tourism Products and Services, Travel Agency Management, Hospitality Industry, Sustainable Tourism, Tourism Marketing, Tourism in Tanzania."));
        library.add(Book.load("Agriculture", "TIE", "T039", "Tie", "A-Level", "Form 5",
            "Agriculture for Advanced Secondary Schools Form Five covers modern agricultural practices, crop production, animal husbandry, and agribusiness. The book follows the 2023 Agriculture syllabus.<br><br>Topics: Principles of Agriculture, Crop Production and Management, Animal Production, Soil Science, Agricultural Economics, Farm Management, Agribusiness, Agricultural Research."));
        library.add(Book.load("Physical Chemistry", "TIE", "T040", "Tie", "A-Level", "Form 5",
            "Physical Chemistry for Advanced Secondary Schools Form Five covers the fundamental principles of physical chemistry including thermodynamics, kinetics, and quantum chemistry. The book follows the 2023 Chemistry syllabus.<br><br>Topics: Atomic Structure, Chemical Kinetics, Chemical Equilibrium, Thermochemistry, Electrochemistry, Quantum Mechanics, Spectroscopy."));
        library.add(Book.load("Historia ya Tanzania na Maadili", "TIE", "T041", "Tie", "A-Level", "Form 5",
            "Historia ya Tanzania na Maadili kwa Kidato cha Tano inajikita katika historia ya Tanzania pamoja na maadili.<br><br>Topics: Historia ya Tanzania Kabla ya Ukoloni, Ukoloni na Utawala wa Kikoloni, Mapambano ya Uhuru, Tanzania Baada ya Uhuru, Maadili na Uraia, Haki za Binadamu."));
        library.add(Book.load("Microeconomics", "TIE", "T042", "Tie", "A-Level", "Form 5",
            "Microeconomics for Advanced Secondary Schools Form Five covers the principles of microeconomic theory including supply and demand, market structures, and consumer behavior. The book follows the 2023 Economics syllabus.<br><br>Topics: Introduction to Economics, Demand and Supply Theory, Elasticity, Consumer Behavior, Production Theory, Market Structures, Factor Pricing, Welfare Economics."));
        library.add(Book.load("French", "TIE", "T043", "Tie", "A-Level", "Form 5",
            "French for Advanced Secondary Schools Form Five covers French language skills including grammar, vocabulary, reading, writing, speaking, and listening. The book follows the 2023 French syllabus.<br><br>Topics: Grammaire Francaise, Vocabulaire et Expressions, Comprehension Ecrite, Expression Ecrite, Expression Orale, Civilisation Francaise, Litterature Francaise."));
        library.add(Book.load("Physics", "TIE", "T044", "Tie", "A-Level", "Form 5",
            "Physics for Advanced Secondary Schools Form Five covers fundamental physics concepts including mechanics, waves, optics, electricity, magnetism, and modern physics. The book follows the 2023 Physics syllabus.<br><br>Topics: Mechanics and Properties of Matter, Waves and Optics, Electricity and Magnetism, Thermodynamics, Atomic and Nuclear Physics, Electronics, Measurement and Error Analysis."));

        // ── Form 6 Books (from TIE self-hosted) ──
        library.add(Book.load("Academic Communication", "TIE", "T100", "Tie", "A-Level", "Form 6",
            "Academic Communication for Advanced Secondary Schools Form Six builds on Form 5 skills with advanced academic writing, research, and scholarly discourse. Follows the 2023 syllabus.<br><br>Topics: Advanced Academic Writing, Research Methodology and Proposal Writing, Scholarly Presentations, Academic Publishing, Critical Analysis and Argumentation, Referencing and Citation Styles."));
        library.add(Book.load("Accountancy", "TIE", "T101", "Tie", "A-Level", "Form 6",
            "Advanced Accountancy Form Six covers advanced financial accounting, company accounts, and management accounting. Follows the 2023 syllabus.<br><br>Topics: Company Accounts, Financial Statement Analysis, Management Accounting, Budgeting and Forecasting, Auditing, Taxation, Advanced Partnership Accounts."));
        library.add(Book.load("Agriculture", "TIE", "T102", "Tie", "A-Level", "Form 6",
            "Agriculture for Advanced Secondary Schools Form Six covers advanced agricultural science, agribusiness management, and sustainable farming. Follows the 2023 syllabus.<br><br>Topics: Advanced Crop Science, Animal Breeding and Genetics, Agricultural Policy, Agribusiness Management, Agricultural Extension, Research Methods in Agriculture."));
        library.add(Book.load("Arabic", "TIE", "T103", "Tie", "A-Level", "Form 6",
            "Arabic for Advanced Secondary Schools Form Six advances Arabic language proficiency in grammar, literature, and scholarly communication.<br><br>Topics: Advanced Arabic Grammar (Nahw), Arabic Morphology (Sarf), Arabic Literature (Adab), Rhetoric (Balaghah), Translation Studies, Scholarly Writing."));
        library.add(Book.load("Biology", "TIE", "T104", "Tie", "A-Level", "Form 6",
            "Advanced Biology Form Six covers advanced topics in cytology, genetics, biotechnology, evolution, and ecology. Follows the 2023 syllabus.<br><br>Topics: Advanced Cell Biology, Molecular Genetics, Biotechnology and Genetic Engineering, Evolution and Speciation, Advanced Ecology, Plant and Animal Physiology, Microbiology."));
        library.add(Book.load("Business Studies", "TIE", "T105", "Tie", "A-Level", "Form 6",
            "Business Studies for Advanced Secondary Schools Form Six covers strategic management, international business, and advanced entrepreneurship.<br><br>Topics: Strategic Management, International Business, Advanced Marketing, Financial Management, Human Resource Management, Business Research Methods, Business Law and Ethics."));
        library.add(Book.load("Chinese", "TIE", "T106", "Tie", "A-Level", "Form 6",
            "Chinese for Advanced Secondary Schools Form Six advances Chinese language skills in reading, writing, speaking, and listening.<br><br>Topics: Advanced Chinese Grammar, Chinese Literature, Academic Writing in Chinese, Chinese Media and Communication, Translation and Interpretation, Chinese Business Culture."));
        library.add(Book.load("Computer Science", "TIE", "T107", "Tie", "A-Level", "Form 6",
            "Computer Science Form Six covers advanced programming, data structures, algorithms, software engineering, and networking.<br><br>Topics: Object-Oriented Programming, Data Structures and Algorithms, Database Management Systems, Software Engineering, Computer Networking, Web Development, Artificial Intelligence."));
        library.add(Book.load("Divinity", "TIE", "T108", "Tie", "A-Level", "Form 6",
            "Divinity for Advanced Secondary Schools Form Six covers advanced theological studies, biblical exegesis, and religious philosophy.<br><br>Topics: Advanced Biblical Studies, Theological Ethics, Philosophy of Religion, Liberation Theology, Interfaith Dialogue, Pastoral Counseling."));
        library.add(Book.load("English", "TIE", "T109", "Tie", "A-Level", "Form 6",
            "English for Advanced Secondary Schools Form Six develops advanced English language and literary analysis skills.<br><br>Topics: Advanced Grammar and Style, Critical Reading and Analysis, Academic Writing, Oral Communication, Literary Appreciation, Research Writing."));
        library.add(Book.load("Fasihi ya Kiswahili", "TIE", "T110", "Tie", "A-Level", "Form 6",
            "Fasihi ya Kiswahili Shule za Sekondari Kidato cha Sita inajikita katika uchambuzi wa kina wa fasihi na nadharia.<br><br>Topics: Nadharia za Fasihi, Uhakiki wa Riwaya na Tamthilia, Uchambuzi wa Ushairi, Fasihi Linganishi, Fasihi na Jinsia, Fasihi na Jamii."));
        library.add(Book.load("Fine Art", "TIE", "T111", "Tie", "A-Level", "Form 6",
            "Fine Art for Advanced Secondary Schools Form Six covers advanced art techniques, art history, and artistic expression.<br><br>Topics: Advanced Drawing and Painting, Sculpture and Ceramics, Printmaking, Art History and Criticism, Contemporary African Art, Portfolio Development, Art Exhibition."));
        library.add(Book.load("Food and Human Nutrition", "TIE", "T112", "Tie", "A-Level", "Form 6",
            "Food and Human Nutrition for Advanced Secondary Schools Form Six covers advanced nutrition science, diet therapy, and food technology.<br><br>Topics: Advanced Nutrition Biochemistry, Diet Therapy and Counseling, Food Microbiology, Food Processing Technology, Nutritional Assessment, Community Nutrition."));
        library.add(Book.load("French", "TIE", "T113", "Tie", "A-Level", "Form 6",
            "French for Advanced Secondary Schools Form Six advances French language proficiency in literature, composition, and oral expression.<br><br>Topics: Litterature Francaise, Grammaire Avancee, Composition et Redaction, Civilisation et Culture, Traduction, Expression Orale Avancee."));
        library.add(Book.load("Historia ya Tanzania na Maadili", "TIE", "T114", "Tie", "A-Level", "Form 6",
            "Historia ya Tanzania na Maadili Kidato cha Sita inajikita katika historia ya kisasa ya Tanzania na maadili ya taifa.<br><br>Topics: Tanzania na Siasa za Kisasa, Uchumi na Maendeleo, Jinsia na Maendeleo, Amani na Usalama, Maadili ya Kiongozi na Uraia."));
        library.add(Book.load("History", "TIE", "T115", "Tie", "A-Level", "Form 6",
            "History for Advanced Secondary Schools Form Six covers world history, international relations, and contemporary global issues.<br><br>Topics: World War I and II, The Cold War, Decolonization and Independence Movements, International Organizations, Globalization, Contemporary Global Issues, Historiography."));
        library.add(Book.load("Kiswahili", "TIE", "T116", "Tie", "A-Level", "Form 6",
            "Kiswahili Shule za Sekondari Kidato cha Sita kinajikita katika isimu, fasihi, na utamaduni wa Kiswahili.<br><br>Topics: Isimu ya Kiswahili, Semantiki na Pragmatiki, Uhakiki wa Fasihi, Lugha na Jamii, Utafiti wa Kiswahili, Staili na Uandishi wa Kisanifu."));
        library.add(Book.load("Literature in English", "TIE", "T117", "Tie", "A-Level", "Form 6",
            "Literature in English for Advanced Secondary Schools Form Six covers advanced literary analysis of prose, poetry, and drama.<br><br>Topics: Advanced Literary Theory, Shakespearean Studies, Modernist Literature, Postcolonial Literature, Comparative Literature, Literary Criticism, Creative Writing."));
        library.add(Book.load("Macroeconomics", "TIE", "T118", "Tie", "A-Level", "Form 6",
            "Macroeconomics for Advanced Secondary Schools Form Six covers macroeconomic theory, national income, monetary and fiscal policy, and international economics.<br><br>Topics: National Income Accounting, Aggregate Demand and Supply, Monetary Policy, Fiscal Policy, Inflation and Unemployment, International Trade and Finance, Economic Growth and Development."));
        library.add(Book.load("Mathematics", "TIE", "T119", "Tie", "A-Level", "Form 6",
            "Mathematics for Advanced Secondary Schools Form Six covers advanced mathematics including calculus, statistics, and linear algebra.<br><br>Topics: Advanced Calculus, Differential Equations, Linear Algebra, Probability and Statistics, Numerical Methods, Mathematical Modeling, Vectors and Mechanics."));
        library.add(Book.load("Music", "TIE", "T120", "Tie", "A-Level", "Form 6",
            "Music for Advanced Secondary Schools Form Six covers advanced music theory, composition, and performance.<br><br>Topics: Advanced Music Theory, Harmony and Counterpoint, Music Analysis, Composition and Arrangement, Performance Practice, East African Music, Music Technology."));
        library.add(Book.load("Physics", "TIE", "T121", "Tie", "A-Level", "Form 6",
            "Physics for Advanced Secondary Schools Form Six covers advanced physics concepts in mechanics, electromagnetism, modern physics, and astrophysics.<br><br>Topics: Advanced Mechanics, Electromagnetic Fields and Waves, Quantum Physics, Nuclear Physics, Astrophysics and Cosmology, Electronics, Thermodynamics."));
        library.add(Book.load("Sport Studies", "TIE", "T122", "Tie", "A-Level", "Form 6",
            "Sport Studies for Advanced Secondary Schools Form Six covers sports science, exercise physiology, and sports management.<br><br>Topics: Exercise Physiology, Sports Psychology, Sports Nutrition, Sports Medicine, Coaching and Officiating, Sports Management, Recreation and Leisure Studies."));
        library.add(Book.load("Textiles and Garment Construction", "TIE", "T123", "Tie", "A-Level", "Form 6",
            "Textiles and Garment Construction for Secondary Schools Form Six covers advanced textile science, fashion design, and garment production.<br><br>Topics: Textile Science and Technology, Fashion Design and Illustration, Pattern Drafting and Grading, Garment Construction Techniques, Textile Testing and Quality Control, Fashion Marketing."));
        library.add(Book.load("Theatre Arts", "TIE", "T124", "Tie", "A-Level", "Form 6",
            "Theatre Arts for Advanced Secondary Schools Form Six covers advanced performance, directing, and theatrical production.<br><br>Topics: Advanced Acting Techniques, Directing and Production, Theatre History, Play Analysis, Contemporary Theatre, Film and Drama, Theatre Management."));
        library.add(Book.load("Tourism", "TIE", "T125", "Tie", "A-Level", "Form 6",
            "Tourism for Advanced Secondary Schools Form Six covers advanced tourism management, ecotourism, and tourism policy.<br><br>Topics: Tourism Policy and Planning, Ecotourism and Sustainable Tourism, Tourism Economics, Hospitality Management, Tour Operations, Destination Marketing, Cultural Tourism."));

        // ── Form 4 Books (from TIE self-hosted, O-Level) ──
        library.add(Book.load("Basic Mathematics", "TIE", "T045", "Tie", "Secondary", "Form 4",
            "Basic Mathematics for Secondary Schools Student's Book Form Four covers fundamental mathematical concepts and problem-solving skills.<br><br>Topics: Numbers and Operations, Algebra, Geometry, Trigonometry, Statistics, Probability, Matrices and Transformations, Linear Programming."));
        library.add(Book.load("Biology", "TIE", "T046", "Tie", "Secondary", "Form 4",
            "Biology for Secondary Schools Student's Book Form Four covers biological concepts and principles.<br><br>Topics: Cell Biology, Genetics and Evolution, Ecology, Classification of Living Organisms, Plant and Animal Physiology, Reproduction and Growth, Health and Disease."));
        library.add(Book.load("Chemistry", "TIE", "T047", "Tie", "Secondary", "Form 4",
            "Chemistry for Secondary Schools Student's Book Form Four covers chemical principles and laboratory practices.<br><br>Topics: Atomic Structure and Bonding, Chemical Reactions, Stoichiometry, Acids and Bases, Electrochemistry, Organic Chemistry, Environmental Chemistry."));
        library.add(Book.load("Physics", "TIE", "T048", "Tie", "Secondary", "Form 4",
            "Physics for Secondary Schools Student's Book Form Four covers fundamental physics concepts and applications.<br><br>Topics: Mechanics, Waves and Optics, Electricity and Magnetism, Electronics, Atomic and Nuclear Physics, Thermal Physics, Measurement and Analysis."));
        library.add(Book.load("History", "TIE", "T049", "Tie", "Secondary", "Form 4",
            "History for Secondary Schools Student's Book Form Four covers African and world history.<br><br>Topics: Pre-colonial Africa, The Slave Trade, European Colonization, Colonial Administration, African Nationalism, Independence Movements, Post-independence Africa."));
        library.add(Book.load("Geography", "TIE", "T050", "Tie", "Secondary", "Form 4",
            "Geography for Secondary Schools Student's Book Form Four covers physical and human geography.<br><br>Topics: Earth's Structure and Landforms, Weather and Climate, Soils and Vegetation, Map Reading and Interpretation, Environmental Conservation, Population and Settlement, Economic Activities."));
        library.add(Book.load("English", "TIE", "T051", "Tie", "Secondary", "Form 4",
            "English for Secondary Schools Student's Book Form Four develops English language skills.<br><br>Topics: Reading and Comprehension, Writing Skills, Grammar and Usage, Oral Communication, Literary Analysis, Summary Writing, Note Making."));
        library.add(Book.load("Kiswahili", "TIE", "T052", "Tie", "Secondary", "Form 4",
            "Kiswahili kwa Shule za Sekondari Kitabu cha Mwanafunzi Kidato cha Nne kinajikita katika lugha na fasihi ya Kiswahili.<br><br>Topics: Sarufi na Matumizi ya Lugha, Ufahamu na Ufupisho, Insha na Utungaji, Fasihi Simulizi na Andishi, Uhaktiki wa Fasihi, Lugha na Jamii."));
        library.add(Book.load("Civics", "TIE", "T053", "Tie", "Secondary", "Form 4",
            "Civics for Secondary Schools Student's Book Form Four covers civic education and responsibilities.<br><br>Topics: Nation and Nationalism, Democracy and Human Rights, Government and Governance, Citizenship and Patriotism, Conflict Resolution, Economic Development, Global Issues."));
        library.add(Book.load("Information and Computer Studies", "TIE", "T054", "Tie", "Secondary", "Form 4",
            "Information and Computer Studies for Secondary Schools Student's Book Form Four covers computer fundamentals and ICT applications.<br><br>Topics: Computer Basics, Data Representation, Problem Solving, Programming Concepts, Internet and Web Technologies, Database Management, System Development."));
        library.add(Book.load("Book-Keeping", "TIE", "T055", "Tie", "Secondary", "Form 4",
            "Book-Keeping for Secondary Schools Student's Book Form Four covers accounting principles and practices.<br><br>Topics: Principles of Book-Keeping, Books of Original Entry, Ledger Posting and Trial Balance, Final Accounts, Cash Books and Bank Reconciliation, Control Accounts, Depreciation, Partnership Accounts."));
        library.add(Book.load("Commerce", "TIE", "T056", "Tie", "Secondary", "Form 4",
            "Commerce for Secondary Schools Student's Book Form Four covers commercial and business principles.<br><br>Topics: Introduction to Commerce, Trade and Distribution, Retail and Wholesale Trade, Transport and Communication, Banking and Finance, Insurance, Business Organization, International Trade."));
        library.add(Book.load("Agriculture", "TIE", "T057", "Tie", "Secondary", "Form 4",
            "Agriculture for Secondary Schools Student's Book Form Four covers agricultural principles and practices.<br><br>Topics: Principles of Agriculture, Crop Production, Animal Production, Soil Science and Conservation, Farm Tools and Machinery, Agricultural Economics, Agribusiness, Environmental Agriculture."));

        // ── Form 3 Books (from TIE self-hosted, O-Level) ──
        library.add(Book.load("Basic Mathematics", "TIE", "T058", "Tie", "Secondary", "Form 3",
            "Basic Mathematics for Secondary Schools Student's Book Form Three covers fundamental mathematical concepts.<br><br>Topics: Numbers and Operations, Algebra, Geometry, Trigonometry, Statistics, Probability, Matrices and Transformations, Linear Programming."));
        library.add(Book.load("Chemistry", "TIE", "T059", "Tie", "Secondary", "Form 3",
            "Chemistry for Secondary Schools Student's Book Form Three covers chemical principles.<br><br>Topics: Atomic Structure and Bonding, Chemical Reactions, Stoichiometry, Acids and Bases, Electrochemistry, Organic Chemistry, Environmental Chemistry."));
        library.add(Book.load("Physics", "TIE", "T060", "Tie", "Secondary", "Form 3",
            "Physics for Secondary Schools Student's Book Form Three covers fundamental physics concepts.<br><br>Topics: Mechanics, Waves and Optics, Electricity and Magnetism, Electronics, Atomic and Nuclear Physics, Thermal Physics, Measurement and Analysis."));
        library.add(Book.load("Biology", "TIE", "T061", "Tie", "Secondary", "Form 3",
            "Biology for Secondary Schools Student's Book Form Three covers biological concepts.<br><br>Topics: Cell Biology, Genetics and Evolution, Ecology, Classification of Living Organisms, Plant and Animal Physiology, Reproduction and Growth, Health and Disease."));
        library.add(Book.load("History", "TIE", "T062", "Tie", "Secondary", "Form 3",
            "History for Secondary Schools Student's Book Form Three covers African and world history.<br><br>Topics: Pre-colonial Africa, The Slave Trade, European Colonization, Colonial Administration, African Nationalism, Independence Movements, Post-independence Africa."));
        library.add(Book.load("Geography", "TIE", "T063", "Tie", "Secondary", "Form 3",
            "Geography for Secondary Schools Student's Book Form Three covers physical and human geography.<br><br>Topics: Earth's Structure and Landforms, Weather and Climate, Soils and Vegetation, Map Reading and Interpretation, Environmental Conservation, Population and Settlement, Economic Activities."));
        library.add(Book.load("English", "TIE", "T064", "Tie", "Secondary", "Form 3",
            "English for Secondary Schools Student's Book Form Three develops English language skills.<br><br>Topics: Reading and Comprehension, Writing Skills, Grammar and Usage, Oral Communication, Literary Analysis, Summary Writing, Note Making."));
        library.add(Book.load("Kiswahili", "TIE", "T065", "Tie", "Secondary", "Form 3",
            "Kiswahili kwa Shule za Sekondari Kitabu cha Mwanafunzi Kidato cha Tatu kinajikita katika lugha na fasihi ya Kiswahili.<br><br>Topics: Sarufi na Matumizi ya Lugha, Ufahamu na Ufupisho, Insha na Utungaji, Fasihi Simulizi na Andishi, Uhaktiki wa Fasihi, Lugha na Jamii."));
        library.add(Book.load("Civics", "TIE", "T066", "Tie", "Secondary", "Form 3",
            "Civics for Secondary Schools Student's Book Form Three covers civic education.<br><br>Topics: Nation and Nationalism, Democracy and Human Rights, Government and Governance, Citizenship and Patriotism, Conflict Resolution, Economic Development, Global Issues."));
        library.add(Book.load("Information and Computer Studies", "TIE", "T067", "Tie", "Secondary", "Form 3",
            "Information and Computer Studies for Secondary Schools Student's Book Form Three covers computer fundamentals.<br><br>Topics: Computer Basics, Data Representation, Problem Solving, Programming Concepts, Internet and Web Technologies, Database Management, System Development."));
        library.add(Book.load("Book-Keeping", "TIE", "T068", "Tie", "Secondary", "Form 3",
            "Book-Keeping for Secondary Schools Student's Book Form Three covers accounting principles.<br><br>Topics: Principles of Book-Keeping, Books of Original Entry, Ledger Posting and Trial Balance, Final Accounts, Cash Books and Bank Reconciliation, Control Accounts, Depreciation, Partnership Accounts."));
        library.add(Book.load("Commerce", "TIE", "T069", "Tie", "Secondary", "Form 3",
            "Commerce for Secondary Schools Student's Book Form Three covers commercial principles.<br><br>Topics: Introduction to Commerce, Trade and Distribution, Retail and Wholesale Trade, Transport and Communication, Banking and Finance, Insurance, Business Organization, International Trade."));
        library.add(Book.load("Agriculture", "TIE", "T070", "Tie", "Secondary", "Form 3",
            "Agriculture for Secondary Schools Student's Book Form Three covers agricultural principles.<br><br>Topics: Principles of Agriculture, Crop Production, Animal Production, Soil Science and Conservation, Farm Tools and Machinery, Agricultural Economics, Agribusiness, Environmental Agriculture."));

        // ── Form 3 Extra Books ──
        library.add(Book.load("Literature in English", "TIE", "T071", "Tie", "Secondary", "Form 3",
            "Literature in English for Secondary Schools Student's Book Form Three covers literary analysis.<br><br>Topics: Prose, Poetry, Drama, Oral Literature, Literary Criticism, African Literature, Essay Writing."));
        library.add(Book.load("French", "TIE", "T072", "Tie", "Secondary", "Form 3",
            "Bonjour 3! Methode de Francais Student's Book Form Three covers French language skills.<br><br>Topics: Grammaire, Vocabulaire, Comprehension Ecrite, Expression Ecrite, Expression Orale, Civilisation, Litterature."));
        library.add(Book.load("Chinese", "TIE", "T073", "Tie", "Secondary", "Form 3",
            "Chinese for Ordinary Secondary Schools Student's Book Form Three covers Chinese language skills.<br><br>Topics: Pronunciation, Grammar, Vocabulary, Reading, Writing, Speaking, Chinese Culture."));
        library.add(Book.load("Arabic", "TIE", "T074", "Tie", "Secondary", "Form 3",
            "Arabic for Secondary Schools Student's Book Form Three covers Arabic language skills.<br><br>Topics: Arabic Grammar (Nahw), Morphology (Sarf), Reading Comprehension, Writing Skills, Speaking and Listening, Islamic Texts."));
        library.add(Book.load("Historia ya Tanzania na Maadili", "TIE", "T075", "Tie", "Secondary", "Form 3",
            "Historia ya Tanzania na Maadili Litabu cha Mwanafunzi Kidato cha Tatu linajikita katika historia ya Tanzania na maadili.<br><br>Topics: Historia ya Tanzania, Maadili na Uraia, Utamaduni na Jamii, Haki za Binadamu, Demokrasia."));
        library.add(Book.load("Food and Human Nutrition", "TIE", "T076", "Tie", "Secondary", "Form 3",
            "Food and Human Nutrition for Secondary Schools Student's Book Form Three covers nutrition and food science.<br><br>Topics: Introduction to Nutrition, Macronutrients and Micronutrients, Meal Planning, Food Preparation, Nutritional Disorders, Food Science."));
        library.add(Book.load("Textiles and Garment Construction", "TIE", "T077", "Tie", "Secondary", "Form 3",
            "Textiles and Garment Construction for Secondary Schools Student's Book Form Three covers textile science and garment making.<br><br>Topics: Textile Science, Fashion Design, Pattern Drafting, Garment Construction, Fabric Selection, Finishing Techniques."));
        library.add(Book.load("Business Studies", "TIE", "T078", "Tie", "Secondary", "Form 3",
            "Business Studies for Secondary Schools Student's Book Form Three covers business principles.<br><br>Topics: Business Environment, Entrepreneurship, Business Management, Marketing, Production, Business Finance, Business Ethics."));

        // ── More Form 4 Books ──
        library.add(Book.load("Food and Human Nutrition", "TIE", "T079", "Tie", "Secondary", "Form 4",
            "Food and Human Nutrition for Secondary Schools Student's Book Form Four covers nutrition and food science.<br><br>Topics: Introduction to Nutrition, Macronutrients and Micronutrients, Meal Planning, Food Preparation, Nutritional Disorders, Food Science and Technology."));
        library.add(Book.load("Textiles and Garment Construction", "TIE", "T080", "Tie", "Secondary", "Form 4",
            "Textiles and Garment Construction for Secondary Schools Student's Book Form Four covers textile science and garment making.<br><br>Topics: Textile Science and Technology, Fashion Design and Illustration, Pattern Drafting and Grading, Garment Construction Techniques, Textile Testing, Fashion Marketing."));

        // ── Form 1 Books (from TIE self-hosted, O-Level) ──
        library.add(Book.load("Fine Art", "TIE", "T219", "Tie", "Secondary", "Form 1",
            "Fine Art for Secondary Schools Student's Book Form One covers art techniques and appreciation.<br><br>Topics: Drawing and Painting, Sculpture and Ceramics, Printmaking, Art History and Criticism, Contemporary African Art, Portfolio Development."));
        library.add(Book.load("French", "TIE", "T220", "Tie", "Secondary", "Form 1",
            "Bonjour 1! Méthode de français Livre de l'élève pour la première année Enseignement secondaire covers French language skills.<br><br>Topics: Grammaire, Vocabulaire, Compréhension Écrite, Expression Écrite, Expression Orale, Civilisation, Littérature."));
        library.add(Book.load("Music and Sound Technology", "TIE", "T221", "Tie", "Secondary", "Form 1",
            "Music and Sound Technology for Secondary Schools covers music production and sound engineering.<br><br>Topics: Sound Fundamentals, Recording Equipment, Mixing and Mastering, Digital Audio Workstations, Live Sound, Music Production, Audio Editing, Sound Design."));
        library.add(Book.load("Motor Vehicle Mechanics", "TIE", "T222", "Tie", "Secondary", "Form 1",
            "Motor Vehicle Mechanics for Secondary Schools covers automotive engineering and repair.<br><br>Topics: Engine Principles, Transmission Systems, Braking Systems, Steering and Suspension, Electrical Systems, Engine Tune-up, Diagnostics and Troubleshooting, Safety Practices."));
        library.add(Book.load("Netball Performance", "TIE", "T223", "Tie", "Secondary", "Form 1",
            "Netball Performance for Secondary Schools covers netball training and techniques.<br><br>Topics: Netball Rules and Regulations, Passing Techniques, Shooting Skills, Defensive Strategies, Court Positioning, Fitness Training, Team Tactics, Game Analysis."));
        library.add(Book.load("Music Performance", "TIE", "T224", "Tie", "Secondary", "Form 1",
            "Music Performance for Secondary Schools covers vocal and instrumental performance skills.<br><br>Topics: Vocal Techniques, Instrumental Performance, Music Theory, Sight Reading, Ensemble Playing, Stage Presence, Repertoire Development, Performance Practice."));
        library.add(Book.load("Wood Processing", "TIE", "T225", "Tie", "Secondary", "Form 1",
            "Wood Processing for Secondary Schools covers woodworking and timber processing techniques.<br><br>Topics: Timber and Wood Products, Woodworking Tools, Cutting and Shaping, Joinery Techniques, Finishing and Polishing, Furniture Making, Safety Practices."));
        library.add(Book.load("Welding and Metal Fabrication", "TIE", "T226", "Tie", "Secondary", "Form 1",
            "Welding and Metal Fabrication for Secondary Schools covers metal joining, cutting, and fabrication techniques.<br><br>Topics: Introduction to Welding, Arc Welding, Gas Welding, Metal Cutting, Fabrication Techniques, Safety in Welding, Project Work."));
        library.add(Book.load("Track Events", "TIE", "T227", "Tie", "Secondary", "Form 1",
            "Track Events for Secondary Schools covers athletic track events training and techniques.<br><br>Topics: Sprinting, Middle Distance Running, Long Distance Running, Hurdles, Relay Races, Starting Techniques, Training Methods."));
        library.add(Book.load("Tour Guiding", "TIE", "T228", "Tie", "Secondary", "Form 1",
            "Tour Guiding for Secondary Schools covers tourism principles and guiding techniques.<br><br>Topics: Introduction to Tourism, Tour Guiding Skills, Customer Service, Destination Knowledge, Communication Skills, Tour Planning, Ethics in Tour Guiding."));
        library.add(Book.load("Technical Drawing", "TIE", "T229", "Tie", "Secondary", "Form 1",
            "Technical Drawing for Secondary Schools covers drafting and design principles.<br><br>Topics: Drawing Instruments and Techniques, Geometric Construction, Orthographic Projection, Isometric Drawing, Dimensioning, Sections and Developments, Computer-Aided Design."));
        library.add(Book.load("Solar Power Installation", "TIE", "T230", "Tie", "Secondary", "Form 1",
            "Solar Power Installation for Secondary Schools covers solar energy principles and installation techniques.<br><br>Topics: Solar Energy Fundamentals, Solar Panel Types, System Design, Installation Procedures, Battery Storage, Maintenance and Troubleshooting, Safety Practices."));
        library.add(Book.load("Refrigeration and Air Conditioning", "TIE", "T231", "Tie", "Secondary", "Form 1",
            "Refrigeration and Air Conditioning for Secondary Schools covers cooling system principles and repair.<br><br>Topics: Refrigeration Principles, Refrigerants, Compressors, Condensers and Evaporators, Air Conditioning Systems, Installation and Maintenance, Troubleshooting and Repair, Safety Practices."));
        library.add(Book.load("Plumbing and Pipe Fittings", "TIE", "T232", "Tie", "Secondary", "Form 1",
            "Plumbing and Pipe Fittings for Secondary Schools covers plumbing installation and maintenance.<br><br>Topics: Plumbing Tools and Equipment, Pipe Materials and Joining, Water Supply Systems, Drainage Systems, Sanitary Fixtures, Water Heaters, Plumbing Codes and Safety."));
        library.add(Book.load("Painting and Signwriting", "TIE", "T233", "Tie", "Secondary", "Form 1",
            "Painting and Signwriting for Secondary Schools covers painting techniques and sign design.<br><br>Topics: Painting Tools and Materials, Surface Preparation, Color Mixing, Painting Techniques, Sign Design and Layout, Lettering and Typography, Sign Production, Safety Practices."));
        library.add(Book.load("Ngoma", "TIE", "T234", "Tie", "Secondary", "Form 1",
            "Ngoma for Secondary Schools covers traditional dance and performance.<br><br>Topics: Introduction to Ngoma, Traditional Dance Forms, Rhythm and Movement, Costume and Adornment, Cultural Significance, Performance Techniques, Choreography, Preservation of Heritage."));
        library.add(Book.load("Bible Knowledge", "TIE", "T235", "Tie", "Secondary", "Form 1",
            "Bible Knowledge for Secondary Schools Student's Book Form One covers biblical studies.<br><br>Topics: Old Testament Survey, New Testament Survey, The Gospels, Acts of the Apostles, Pauline Epistles, General Epistles, Biblical Themes and Teachings, Christian Living."));
        library.add(Book.load("Elimu ya Dini ya Kiislamu", "TIE", "T236", "Tie", "Secondary", "Form 1",
            "Elimu ya Dini ya Kiislamu kwa Kidato cha Kwanza covers Islamic religious education.<br><br>Topics: Imani na Misingi ya Uislamu, Qur'an na Hadith, Ibada na Maadili, Historia ya Uislamu, Fiqh na Sheria, Maadili ya Kiislamu, Elimu ya Mtume."));
        library.add(Book.load("Historia ya Tanzania na Maadili", "TIE", "T237", "Tie", "Secondary", "Form 1",
            "Historia ya Tanzania na Maadili Kitabu cha Mwanafunzi Kidato cha Kwanza covers Tanzanian history and ethics.<br><br>Topics: Historia ya Tanzania, Maadili na Uraia, Utamaduni na Jamii, Haki za Binadamu, Demokrasia, Uongozi Bora."));
        library.add(Book.load("Chemistry", "TIE", "T238", "Tie", "Secondary", "Form 1",
            "Chemistry for Secondary Schools Student's Book Form One covers chemical principles.<br><br>Topics: Introduction to Chemistry, Laboratory Techniques, Atomic Structure, Chemical Bonding, Acids and Bases, Water and Solutions, Environmental Chemistry."));
        library.add(Book.load("History", "TIE", "T239", "Tie", "Secondary", "Form 1",
            "History for Secondary Schools Student's Book Form One covers historical concepts and events.<br><br>Topics: Introduction to History, Sources of History, Early Human Evolution, Development of Agriculture, Trade and Exchange, Social and Economic Organization, African Societies."));
        library.add(Book.load("Agriculture", "TIE", "T240", "Tie", "Secondary", "Form 1",
            "Agriculture for Secondary Schools Student's Book Form One covers agricultural principles.<br><br>Topics: Principles of Agriculture, Crop Production, Animal Production, Soil Science and Conservation, Farm Tools and Machinery, Agricultural Economics, Agribusiness, Environmental Agriculture."));
        library.add(Book.load("Theatre Arts", "TIE", "T241", "Tie", "Secondary", "Form 1",
            "Theatre Arts for Secondary Schools Student's Book Form One covers the principles of drama and performance.<br><br>Topics: Introduction to Theatre, Acting Techniques, Stage Design and Management, Directing, Playwriting, African Theatre, Performance Analysis."));
        library.add(Book.load("Textiles and Garment Construction", "TIE", "T242", "Tie", "Secondary", "Form 1",
            "Textiles and Garment Construction for Secondary Schools Student's Book Form One covers textile science and garment making.<br><br>Topics: Textile Science and Technology, Fashion Design and Illustration, Pattern Drafting and Grading, Garment Construction Techniques, Textile Testing, Fashion Marketing."));
        library.add(Book.load("Sport Studies", "TIE", "T243", "Tie", "Secondary", "Form 1",
            "Sport Studies for Secondary Schools Student's Book Form One covers sports science and physical education.<br><br>Topics: Exercise Physiology, Sports Psychology, Sports Nutrition, Sports Medicine, Coaching and Officiating, Sports Management, Recreation and Leisure Studies."));
        library.add(Book.load("Physics", "TIE", "T244", "Tie", "Secondary", "Form 1",
            "Physics for Secondary Schools Student's Book Form One covers fundamental physics concepts.<br><br>Topics: Measurement, Forces and Motion, Work Energy and Power, Heat and Temperature, Light and Optics, Sound and Waves, Electricity and Magnetism."));
        library.add(Book.load("Music", "TIE", "T245", "Tie", "Secondary", "Form 1",
            "Music for Secondary Schools Student's Book Form One covers music theory and appreciation.<br><br>Topics: Music Theory and Notation, African Music, Western Music History, Instrumental Performance, Vocal Music, Music Composition, Aural Skills."));
        library.add(Book.load("Mathematics", "TIE", "T246", "Tie", "Secondary", "Form 1",
            "Mathematics for Secondary Schools Student's Book Form One covers fundamental mathematical concepts.<br><br>Topics: Numbers and Operations, Algebra, Geometry, Trigonometry, Statistics, Probability, Matrices and Transformations, Linear Programming."));
        library.add(Book.load("Geography", "TIE", "T247", "Tie", "Secondary", "Form 1",
            "Geography for Secondary Schools Student's Book Form One covers physical and human geography.<br><br>Topics: Earth's Structure and Landforms, Weather and Climate, Soils and Vegetation, Map Reading and Interpretation, Environmental Conservation, Population and Settlement, Economic Activities."));
        library.add(Book.load("Food and Human Nutrition", "TIE", "T248", "Tie", "Secondary", "Form 1",
            "Food and Human Nutrition for Secondary Schools Student's Book Form One covers nutrition and food science.<br><br>Topics: Introduction to Nutrition, Macronutrients and Micronutrients, Meal Planning, Food Preparation, Nutritional Disorders, Food Science and Technology."));
        library.add(Book.load("English", "TIE", "T249", "Tie", "Secondary", "Form 1",
            "English for Secondary Schools Student's Book Form One develops English language skills.<br><br>Topics: Reading and Comprehension, Writing Skills, Grammar and Usage, Oral Communication, Literary Analysis, Summary Writing, Note Making."));
        library.add(Book.load("Computer Science", "TIE", "T250", "Tie", "Secondary", "Form 1",
            "Computer Science for Secondary Schools Student's Book Form One covers computer fundamentals.<br><br>Topics: Computer Basics, Data Representation, Problem Solving, Programming Concepts, Internet and Web Technologies, Database Management, System Development."));
        library.add(Book.load("Chinese", "TIE", "T251", "Tie", "Secondary", "Form 1",
            "Chinese for Ordinary Secondary Schools Student's Book Form One covers Chinese language skills.<br><br>Topics: Pronunciation, Grammar, Vocabulary, Reading, Writing, Speaking, Chinese Culture."));
        library.add(Book.load("Business Studies", "TIE", "T252", "Tie", "Secondary", "Form 1",
            "Business Studies for Secondary Schools Student's Book Form One covers business principles.<br><br>Topics: Business Environment, Entrepreneurship, Business Management, Marketing, Production, Business Finance, Business Ethics."));
        library.add(Book.load("Book-Keeping", "TIE", "T253", "Tie", "Secondary", "Form 1",
            "Book-Keeping for Secondary Schools Student's Book Form One covers accounting principles.<br><br>Topics: Principles of Book-Keeping, Books of Original Entry, Ledger Posting and Trial Balance, Final Accounts, Cash Books and Bank Reconciliation, Control Accounts, Depreciation, Partnership Accounts."));
        library.add(Book.load("Biology", "TIE", "T254", "Tie", "Secondary", "Form 1",
            "Biology for Secondary Schools Student's Book Form One covers biological concepts.<br><br>Topics: Cell Biology, Genetics and Evolution, Ecology, Classification of Living Organisms, Plant and Animal Physiology, Reproduction and Growth, Health and Disease."));

        // ── Form 2 Books (from TIE self-hosted, O-Level) ──
        library.add(Book.load("Basic Mathematics", "TIE", "T081", "Tie", "Secondary", "Form 2",
            "Basic Mathematics for Secondary Schools Student's Book Form Two covers fundamental mathematical concepts.<br><br>Topics: Numbers and Operations, Algebra, Geometry, Trigonometry, Statistics, Probability, Matrices and Transformations, Linear Programming."));
        library.add(Book.load("Biology", "TIE", "T082", "Tie", "Secondary", "Form 2",
            "Biology for Secondary Schools Student's Book Form Two covers biological concepts.<br><br>Topics: Cell Biology, Genetics and Evolution, Ecology, Classification of Living Organisms, Plant and Animal Physiology, Reproduction and Growth, Health and Disease."));
        library.add(Book.load("Chemistry", "TIE", "T083", "Tie", "Secondary", "Form 2",
            "Chemistry for Secondary Schools Student's Book Form Two covers chemical principles.<br><br>Topics: Atomic Structure and Bonding, Chemical Reactions, Stoichiometry, Acids and Bases, Electrochemistry, Organic Chemistry, Environmental Chemistry."));
        library.add(Book.load("Physics", "TIE", "T084", "Tie", "Secondary", "Form 2",
            "Physics for Secondary Schools Student's Book Form Two covers fundamental physics concepts.<br><br>Topics: Mechanics, Waves and Optics, Electricity and Magnetism, Electronics, Atomic and Nuclear Physics, Thermal Physics, Measurement and Analysis."));
        library.add(Book.load("Geography", "TIE", "T085", "Tie", "Secondary", "Form 2",
            "Geography for Secondary Schools Student's Book Form Two covers physical and human geography.<br><br>Topics: Earth's Structure and Landforms, Weather and Climate, Soils and Vegetation, Map Reading and Interpretation, Environmental Conservation, Population and Settlement, Economic Activities."));
        library.add(Book.load("English", "TIE", "T086", "Tie", "Secondary", "Form 2",
            "English for Secondary Schools Student's Book Form Two develops English language skills.<br><br>Topics: Reading and Comprehension, Writing Skills, Grammar and Usage, Oral Communication, Literary Analysis, Summary Writing, Note Making."));
        library.add(Book.load("Kiswahili", "TIE", "T087", "Tie", "Secondary", "Form 2",
            "Kiswahili kwa Shule za Sekondari Kitabu cha Mwanafunzi Kidato cha Pili kinajikita katika lugha na fasihi ya Kiswahili.<br><br>Topics: Sarufi na Matumizi ya Lugha, Ufahamu na Ufupisho, Insha na Utungaji, Fasihi Simulizi na Andishi, Uhaktiki wa Fasihi, Lugha na Jamii."));
        library.add(Book.load("Book-Keeping", "TIE", "T088", "Tie", "Secondary", "Form 2",
            "Book-Keeping for Secondary Schools Student's Book Form Two covers accounting principles.<br><br>Topics: Principles of Book-Keeping, Books of Original Entry, Ledger Posting and Trial Balance, Final Accounts, Cash Books and Bank Reconciliation, Control Accounts, Depreciation, Partnership Accounts."));
        library.add(Book.load("Agriculture", "TIE", "T089", "Tie", "Secondary", "Form 2",
            "Agriculture for Secondary Schools Student's Book Form Two covers agricultural principles.<br><br>Topics: Principles of Agriculture, Crop Production, Animal Production, Soil Science and Conservation, Farm Tools and Machinery, Agricultural Economics, Agribusiness, Environmental Agriculture."));
        library.add(Book.load("Food and Human Nutrition", "TIE", "T090", "Tie", "Secondary", "Form 2",
            "Food and Human Nutrition for Secondary Schools Student's Book Form Two covers nutrition and food science.<br><br>Topics: Introduction to Nutrition, Macronutrients and Micronutrients, Meal Planning, Food Preparation, Nutritional Disorders, Food Science and Technology."));
        library.add(Book.load("Textiles and Garment Construction", "TIE", "T091", "Tie", "Secondary", "Form 2",
            "Textiles and Garment Construction for Secondary Schools Student's Book Form Two covers textile science and garment making.<br><br>Topics: Textile Science and Technology, Fashion Design and Illustration, Pattern Drafting and Grading, Garment Construction Techniques, Textile Testing, Fashion Marketing."));
        library.add(Book.load("Information and Computer Studies", "TIE", "T092", "Tie", "Secondary", "Form 2",
            "Information and Computer Studies for Secondary Schools Student's Book Form Two covers computer fundamentals.<br><br>Topics: Computer Basics, Data Representation, Problem Solving, Programming Concepts, Internet and Web Technologies, Database Management, System Development."));

        // ── Form 2 Extra Books ──
        library.add(Book.load("Chinese", "TIE", "T093", "Tie", "Secondary", "Form 2",
            "Chinese for Ordinary Secondary Schools Student's Book Form Two covers Chinese language skills.<br><br>Topics: Pronunciation, Grammar, Vocabulary, Reading, Writing, Speaking, Chinese Culture."));
        library.add(Book.load("Arabic", "TIE", "T094", "Tie", "Secondary", "Form 2",
            "Arabic for Secondary Schools Student's Book Form Two covers Arabic language skills.<br><br>Topics: Arabic Grammar (Nahw), Morphology (Sarf), Reading Comprehension, Writing Skills, Speaking and Listening, Islamic Texts."));
        library.add(Book.load("French", "TIE", "T095", "Tie", "Secondary", "Form 2",
            "Bonjour 2! Methode de Francais Student's Book Form Two covers French language skills.<br><br>Topics: Grammaire, Vocabulaire, Comprehension Ecrite, Expression Ecrite, Expression Orale, Civilisation, Litterature."));
        library.add(Book.load("Business Studies", "TIE", "T096", "Tie", "Secondary", "Form 2",
            "Business Studies for Secondary Schools Student's Book Form Two covers business principles.<br><br>Topics: Business Environment, Entrepreneurship, Business Management, Marketing, Production, Business Finance, Business Ethics."));
        library.add(Book.load("Music", "TIE", "T097", "Tie", "Secondary", "Form 2",
            "Music for Secondary Schools Student's Book Form Two covers music theory and appreciation.<br><br>Topics: Music Theory and Notation, African Music, Western Music History, Instrumental Performance, Vocal Music, Music Composition, Aural Skills."));
        library.add(Book.load("Sport Studies", "TIE", "T098", "Tie", "Secondary", "Form 2",
            "Sport Studies for Secondary Schools Student's Book Form Two covers sports science and physical education.<br><br>Topics: Exercise Physiology, Sports Psychology, Sports Nutrition, Sports Medicine, Coaching and Officiating, Sports Management, Recreation and Leisure Studies."));
        library.add(Book.load("Theatre Arts", "TIE", "T099", "Tie", "Secondary", "Form 2",
            "Theatre Arts for Secondary Schools Student's Book Form Two covers the principles of drama and performance.<br><br>Topics: Introduction to Theatre, Acting Techniques, Stage Design and Management, Directing, Playwriting, African Theatre, Performance Analysis."));
        library.add(Book.load("Fine Art", "TIE", "T150", "Tie", "Secondary", "Form 2",
            "Fine Art for Secondary Schools Student's Book Form Two covers art techniques and appreciation.<br><br>Topics: Drawing and Painting, Sculpture and Ceramics, Printmaking, Art History and Criticism, Contemporary African Art, Portfolio Development."));

        // ── Form 2 Vocational Books ──
        library.add(Book.load("Welding and Metal Fabrication", "TIE", "T200", "Tie", "Secondary", "Form 2",
            "Welding and Metal Fabrication for Secondary Schools covers metal joining, cutting, and fabrication techniques.<br><br>Topics: Introduction to Welding, Arc Welding, Gas Welding, Metal Cutting, Fabrication Techniques, Safety in Welding, Project Work."));
        library.add(Book.load("Track Events", "TIE", "T201", "Tie", "Secondary", "Form 2",
            "Track Events for Secondary Schools covers athletic track events training and techniques.<br><br>Topics: Sprinting, Middle Distance Running, Long Distance Running, Hurdles, Relay Races, Starting Techniques, Training Methods."));
        library.add(Book.load("Tour Guiding", "TIE", "T202", "Tie", "Secondary", "Form 2",
            "Tour Guiding for Secondary Schools covers tourism principles and guiding techniques.<br><br>Topics: Introduction to Tourism, Tour Guiding Skills, Customer Service, Destination Knowledge, Communication Skills, Tour Planning, Ethics in Tour Guiding."));
        library.add(Book.load("Technical Drawing", "TIE", "T203", "Tie", "Secondary", "Form 2",
            "Technical Drawing for Secondary Schools covers drafting and design principles.<br><br>Topics: Drawing Instruments and Techniques, Geometric Construction, Orthographic Projection, Isometric Drawing, Dimensioning, Sections and Developments, Computer-Aided Design."));
        library.add(Book.load("Solar Power Installation", "TIE", "T204", "Tie", "Secondary", "Form 2",
            "Solar Power Installation for Secondary Schools covers solar energy principles and installation techniques.<br><br>Topics: Solar Energy Fundamentals, Solar Panel Types, System Design, Installation Procedures, Battery Storage, Maintenance and Troubleshooting, Safety Practices."));
        library.add(Book.load("Food and Beverage Services and Sales", "TIE", "T205", "Tie", "Secondary", "Form 2",
            "Food and Beverage Services and Sales for Secondary Schools covers hospitality and food service principles.<br><br>Topics: Introduction to Food Service, Menu Planning, Food Preparation, Beverage Service, Customer Service, Sales Techniques, Hygiene and Safety, Event Catering."));
        library.add(Book.load("Field Crop Production", "TIE", "T206", "Tie", "Secondary", "Form 2",
            "Field Crop Production for Secondary Schools covers crop cultivation principles and practices.<br><br>Topics: Introduction to Crop Production, Soil Preparation, Planting Methods, Crop Management, Pest and Disease Control, Harvesting and Storage, Farm Records."));
        library.add(Book.load("Electronics Repair", "TIE", "T207", "Tie", "Secondary", "Form 2",
            "Electronics Repair for Secondary Schools covers electronic device repair and maintenance.<br><br>Topics: Introduction to Electronics, Tools and Equipment, Circuit Analysis, Soldering Techniques, Diagnosis and Troubleshooting, Repair of Home Appliances, Safety Practices."));
        library.add(Book.load("Electrical Installation", "TIE", "T208", "Tie", "Secondary", "Form 2",
            "Electrical Installation for Secondary Schools covers electrical wiring and installation principles.<br><br>Topics: Electrical Principles, Wiring Systems, Lighting Installation, Power Distribution, Circuit Breakers and Fuses, Earthing Systems, Inspection and Testing, Safety Regulations."));
        library.add(Book.load("Computer Application", "TIE", "T209", "Tie", "Secondary", "Form 2",
            "Computer Application for Secondary Schools covers practical computer skills and software applications.<br><br>Topics: Computer Basics, Operating Systems, Word Processing, Spreadsheets, Presentations, Internet and Email, Database Management, File Management."));
        library.add(Book.load("Computer Programming", "TIE", "T210", "Tie", "Secondary", "Form 2",
            "Computer Programming for Secondary Schools covers programming fundamentals and logic.<br><br>Topics: Introduction to Programming, Algorithms and Flowcharts, Programming Languages, Data Types and Variables, Control Structures, Functions and Procedures, Debugging and Testing."));
        library.add(Book.load("Civil Draughting", "TIE", "T211", "Tie", "Secondary", "Form 2",
            "Civil Draughting for Secondary Schools covers civil engineering drawing and design.<br><br>Topics: Drawing Instruments, Building Construction Drawings, Site Plans, Elevations and Sections, Foundation Plans, Structural Details, CAD Basics, Project Work."));
        library.add(Book.load("Refrigeration and Air Conditioning", "TIE", "T212", "Tie", "Secondary", "Form 2",
            "Refrigeration and Air Conditioning for Secondary Schools covers cooling system principles and repair.<br><br>Topics: Refrigeration Principles, Refrigerants, Compressors, Condensers and Evaporators, Air Conditioning Systems, Installation and Maintenance, Troubleshooting and Repair, Safety Practices."));
        library.add(Book.load("Plumbing and Pipe Fittings", "TIE", "T213", "Tie", "Secondary", "Form 2",
            "Plumbing and Pipe Fittings for Secondary Schools covers plumbing installation and maintenance.<br><br>Topics: Plumbing Tools and Equipment, Pipe Materials and Joining, Water Supply Systems, Drainage Systems, Sanitary Fixtures, Water Heaters, Plumbing Codes and Safety."));
        library.add(Book.load("Music and Sound Technology", "TIE", "T214", "Tie", "Secondary", "Form 2",
            "Music and Sound Technology for Secondary Schools covers music production and sound engineering.<br><br>Topics: Sound Fundamentals, Recording Equipment, Mixing and Mastering, Digital Audio Workstations, Live Sound, Music Production, Audio Editing, Sound Design."));
        library.add(Book.load("Art and Design", "TIE", "T215", "Tie", "Secondary", "Form 2",
            "Art and Design for Secondary Schools Vocational Stream covers artistic expression and design principles.<br><br>Topics: Drawing and Illustration, Color Theory, Painting Techniques, Graphic Design, Sculpture, Textile Design, Portfolio Development, Art Appreciation."));
        library.add(Book.load("Carpentry and Joinery", "TIE", "T216", "Tie", "Secondary", "Form 2",
            "Carpentry and Joinery for Secondary Schools covers woodworking skills and techniques.<br><br>Topics: Timber and Materials, Hand Tools, Power Tools, Joints and Joinery, Furniture Making, Finishing Techniques, Project Planning, Safety in Workshop."));
        library.add(Book.load("Agro Mechanics", "TIE", "T217", "Tie", "Secondary", "Form 2",
            "Agro Mechanics for Secondary Schools covers agricultural machinery and maintenance.<br><br>Topics: Farm Power and Machinery, Tractor Operation and Maintenance, Soil Tillage Equipment, Planting and Harvesting Machinery, Irrigation Systems, Farm Workshop, Safety Practices."));
        library.add(Book.load("Historia ya Tanzania na Maadili", "TIE", "T218", "Tie", "Secondary", "Form 2",
            "Historia ya Tanzania na Maadili Kitabu cha Mwanafunzi Kidato cha Pili kinajikita katika historia ya Tanzania na maadili.<br><br>Topics: Historia ya Tanzania, Maadili na Uraia, Utamaduni na Jamii, Haki za Binadamu, Demokrasia, Uongozi Bora."));

        // ── Pre-Primary / Nursery Books (Real TIE New Edition 2023 Curriculum) ──
        library.add(Book.load("Early Arithmetic, Science and ICT Skills", "TIE", "T310", "Tie", "Pre-Primary", "Nursery",
            "Early Arithmetic, Science and ICT Skills Child's Book for Pre-Primary introduces foundational numeracy, scientific observation, and basic ICT concepts through play-based activities. Aligned with the 2023 Pre-primary Curriculum.<br><br>Topics: Number Recognition 1-20, Counting and Sorting, Basic Shapes and Patterns, Simple Addition and Subtraction, Living and Non-Living Things, Weather and Seasons, Using a Computer, Digital Safety."));
        library.add(Book.load("Health and Environment", "TIE", "T311", "Tie", "Pre-Primary", "Nursery",
            "Health and Environment for Pre-Primary teaches young children about personal hygiene, nutrition, safety, and caring for the natural environment. Uses stories, pictures, and hands-on activities.<br><br>Topics: Hand Washing and Hygiene, Brushing Teeth, Healthy Eating, Sleep and Rest, Safety at Home and School, Plants and Animals, Caring for Our Surroundings, Recycling."));
        library.add(Book.load("Early Life Skills", "TIE", "T312", "Tie", "Pre-Primary", "Nursery",
            "Early Life Skills for Pre-Primary helps children develop social, emotional, and practical skills for daily life. The book promotes good values, self-awareness, and cooperation.<br><br>Topics: Myself and My Family, Emotions and Feelings, Sharing and Cooperation, Good Manners, Helping Others, Decision Making, Problem Solving, Personal Belongings."));
        library.add(Book.load("Early Literacy Skills", "TIE", "T313", "Tie", "Pre-Primary", "Nursery",
            "Early Literacy Skills for Pre-Primary introduces young learners to pre-reading and pre-writing skills. Uses colourful illustrations and simple activities to build a strong literacy foundation.<br><br>Topics: Print Awareness, Letter Recognition, Phonological Awareness, Phonics, Vocabulary Development, Simple Sentences, Story Comprehension, Pre-Writing Strokes, Handwriting."));
        library.add(Book.load("Creative Arts and Sports", "TIE", "T314", "Tie", "Pre-Primary", "Nursery",
            "Creative Arts and Sports for Pre-Primary nurtures creativity, self-expression, and physical development. Children explore art, music, dance, and simple sports through fun activities.<br><br>Topics: Drawing and Colouring, Painting and Printing, Paper Craft and Modeling, Action Songs and Rhymes, Rhythm and Movement, Simple Games, Ball Skills, Outdoor Play."));
        library.add(Book.load("Ninaipenda Nchi yangu Tanzania", "TIE", "T315", "Tie", "Pre-Primary", "Nursery",
            "Ninaipenda Nchi yangu Tanzania (I Love My Country Tanzania) for Pre-Primary introduces children to Tanzanian culture, national symbols, and patriotism. Written in Kiswahili to develop early civic awareness.<br><br>Topics: Bendera ya Taifa, Wanyama wa Tanzania, Vyakula vya Kitamaduni, Mavazi ya Jadi, Ngoma na Nyimbo za Taifa, Ramani ya Tanzania, Hifadhi za Taifa, Sherehe za Taifa."));

        // ── Primary / Standard 1 Books (New Syllabus) ──
        library.add(Book.load("Arithmetics", "TIE", "T400", "Tie", "Primary", "Standard 1",
            "Arithmetics for Standard 1 introduces basic number concepts, counting, and simple operations. Uses colourful illustrations and hands-on activities aligned with the new syllabus.<br><br>Topics: Number Recognition 1-100, Counting and Grouping, Addition and Subtraction, Basic Shapes, Measurements, Time and Calendar, Money, Patterns and Sequences."));
        library.add(Book.load("Health and Environment", "TIE", "T401", "Tie", "Primary", "Standard 1",
            "Health and Environment for Standard 1 teaches personal hygiene, nutrition, safety, and environmental awareness. Interactive activities help young learners develop healthy habits.<br><br>Topics: Personal Hygiene, Nutrition and Food Groups, Safety at Home and School, The Human Body, Plants and Animals, Weather and Seasons, Water and Air, Caring for the Environment."));
        library.add(Book.load("Culture, Arts and Sports", "TIE", "T402", "Tie", "Primary", "Standard 1",
            "Culture, Arts and Sports for Standard 1 introduces Tanzanian cultural heritage, creative expression, and physical development. Children explore traditional music, dance, art, and games.<br><br>Topics: Traditional Dances and Songs, Drawing and Painting, Craft Making, Storytelling, Simple Games and Sports, Teamwork, Cultural Festivals, National Symbols."));
        library.add(Book.load("Kusoma", "TIE", "T403", "Tie", "Primary", "Standard 1",
            "Kusoma (Reading) for Standard 1 develops early Kiswahili reading skills. Builds vocabulary, comprehension, and a love for reading through engaging stories and exercises.<br><br>Topics: Kusoma kwa Sauti, Ufahamu wa Kusoma, Msamiati, Sarufi, Kuandika Sentensi, Kusimulia Hadithi, Mashairi, Utambuzi wa Herufi."));
        library.add(Book.load("Learn English", "TIE", "T404", "Tie", "Primary", "Standard 1",
            "Learn English for Standard 1 introduces basic English vocabulary, simple sentences, and everyday communication. Uses songs, pictures, and fun activities.<br><br>Topics: Alphabet and Phonics, Greetings and Introductions, Colours and Numbers, Family and Friends, Food and Animals, Classroom Objects, Simple Commands, Weather and Seasons."));
        library.add(Book.load("Writing", "TIE", "T405", "Tie", "Primary", "Standard 1",
            "Writing for Standard 1 develops fine motor skills and introduces letter formation, handwriting, and simple composition. Progressive exercises build writing confidence.<br><br>Topics: Pre-Writing Strokes, Letter Formation, Word Writing, Sentence Writing, Punctuation, Spelling, Creative Writing, Handwriting Practice."));

        // ── Primary / Standard 1 Books (Kiswahili Medium) ──
        library.add(Book.load("Kuhesabu (Kiswahili Medium Schools)", "TIE", "T406", "Tie", "Primary", "Standard 1",
            "Kuhesabu (Hesabu) kwa Shule za Msingi Darasa la Kwanza (Kiswahili Medium Schools) huanzisha dhana za msingi za namba, kuhesabu, na shughuli rahisi za hesabu. Kinatumia picha za rangi na shughuli za vitendo.<br><br>Topics: Kutambua Namba 1-100, Kuhesabu na Kupanga, Kujumlisha na Kutoa, Maumbo ya Kijiometri, Vipimo, Saa na Kalenda, Pesa, Mfuatano na Miundo."));
        library.add(Book.load("Afya na Mazingira (Kiswahili Medium Schools)", "TIE", "T407", "Tie", "Primary", "Standard 1",
            "Afya na Mazingira kwa Shule za Msingi Darasa la Kwanza (Kiswahili Medium Schools) hufundisha usafi wa mwili, lishe, usalama na uelewa wa mazingira. Shughuli shirikishi humsaidia mtoto kukuza tabia za kiafya.<br><br>Topics: Usafi wa Mwili, Lishe na Makundi ya Vyakula, Usalama Nyumbani na Shuleni, Mwili wa Binadamu, Mimea na Wanyama, Hali ya Hewa, Maji na Hewa, Kutunza Mazingira."));
        library.add(Book.load("Utamaduni Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T408", "Tie", "Primary", "Standard 1",
            "Utamaduni Sanaa na Michezo kwa Shule za Msingi Darasa la Kwanza (Kiswahili Medium Schools) huanzisha urithi wa kitamaduni wa Tanzania, usemi wa kisanii, na ukuaji wa mwili. Watoto huchunguza muziki wa jadi, ngoma, sanaa na michezo.<br><br>Topics: Ngoma na Nyimbo za Jadi, Kuchora na Kupaka Rangi, Uundaji wa Vitu vya Mikono, Hadithi, Michezo Rahisi na Sanaa, Ushirikiano, Sherehe za Kitamaduni, Alama za Taifa."));
        library.add(Book.load("Kuandika (Kiswahili Medium Schools)", "TIE", "T409", "Tie", "Primary", "Standard 1",
            "Kuandika kwa Shule za Msingi Darasa la Kwanza (Kiswahili Medium Schools) huendeleza stadi za uandishi, uundaji wa herufi na utungaji. Mazoezi ya mfululizo hujenga ujasiri wa uandishi.<br><br>Topics: Stadi za Kabla ya Kuandika, Uundaji wa Herufi, Kuandika Maneno, Kuandika Sentensi, Uakifishaji, Tahajia, Uandishi wa Buni, Mazoezi ya Mwandiko."));
        library.add(Book.load("Learn English (Kiswahili Medium Schools)", "TIE", "T410", "Tie", "Primary", "Standard 1",
            "Learn English for Standard 1 (Kiswahili Medium Schools) introduces basic English vocabulary, simple sentences, and everyday communication. Uses songs, pictures, and fun activities.<br><br>Topics: Alphabet and Phonics, Greetings and Introductions, Colours and Numbers, Family and Friends, Food and Animals, Classroom Objects, Simple Commands, Weather and Seasons."));
        library.add(Book.load("Kusoma (Kiswahili Medium Schools)", "TIE", "T411", "Tie", "Primary", "Standard 1",
            "Kusoma kwa Shule za Msingi Darasa la Kwanza (Kiswahili Medium Schools) hukuza stadi za awali za kusoma kwa Kiswahili. Huunda msamiati, ufahamu, na upendo wa kusoma kupitia hadithi na mazoezi.<br><br>Topics: Kusoma kwa Sauti, Ufahamu wa Kusoma, Msamiati, Sarufi, Kuandika Sentensi, Kusimulia Hadithi, Mashairi, Utambuzi wa Herufi."));

        // ── Primary / Standard 2 Books (English Medium) ──
        library.add(Book.load("Arithmetic", "TIE", "T412", "Tie", "Primary", "Standard 2",
            "Arithmetic for Standard 2 builds on basic number concepts, introduces larger numbers, and develops problem-solving skills. Uses engaging activities and real-life examples.<br><br>Topics: Number Recognition 1-1000, Addition and Subtraction, Multiplication Basics, Division Basics, Fractions, Measurements, Time, Money, Shapes and Patterns."));
        library.add(Book.load("Health and Environment", "TIE", "T413", "Tie", "Primary", "Standard 2",
            "Health and Environment for Standard 2 explores personal health, nutrition, safety, and environmental care. Interactive activities promote healthy habits and environmental awareness.<br><br>Topics: Personal Hygiene, Nutrition, Safety Rules, Human Body Systems, Plants and Animals, Weather, Water and Air, Waste Management."));
        library.add(Book.load("Culture, Arts and Sports", "TIE", "T414", "Tie", "Primary", "Standard 2",
            "Culture, Arts and Sports for Standard 2 deepens understanding of Tanzanian culture, creative arts, and physical education. Children explore traditional music, dance, crafts, and games.<br><br>Topics: Traditional Songs and Dances, Drawing and Painting, Crafts, Storytelling, Sports and Games, Teamwork, Cultural Events, National Heritage."));
        library.add(Book.load("Kusoma", "TIE", "T415", "Tie", "Primary", "Standard 2",
            "Kusoma (Reading) for Standard 2 develops Kiswahili reading fluency and comprehension. Builds vocabulary and critical thinking through engaging stories and exercises.<br><br>Topics: Kusoma kwa Sauti, Ufahamu wa Kusoma, Msamiati, Sarufi, Kuandika Sentensi, Kusimulia Hadithi, Mashairi, Utambuzi wa Herufi."));
        library.add(Book.load("Learn English", "TIE", "T416", "Tie", "Primary", "Standard 2",
            "Learn English for Standard 2 builds English vocabulary, grammar, and communication skills. Uses songs, stories, dialogues, and fun activities.<br><br>Topics: Greetings and Introductions, Family and Friends, Food and Animals, Colours and Numbers, Classroom Objects, Simple Grammar, Reading Comprehension, Writing Sentences."));
        library.add(Book.load("Writing", "TIE", "T417", "Tie", "Primary", "Standard 2",
            "Writing for Standard 2 develops handwriting skills and introduces sentence composition, punctuation, and creative writing. Progressive exercises build writing confidence.<br><br>Topics: Letter Formation, Word Writing, Sentence Writing, Punctuation, Spelling, Creative Writing, Handwriting Practice, Paragraph Writing."));

        // ── Primary / Standard 2 Books (Kiswahili Medium) ──
        library.add(Book.load("Kuhesabu (Kiswahili Medium Schools)", "TIE", "T418", "Tie", "Primary", "Standard 2",
            "Kuhesabu kwa Shule za Msingi Darasa la Pili (Kiswahili Medium Schools) hujenga dhana za namba, kujumlisha, kutoa, na kuzidisha. Kinatumia shughuli za vitendo na mifano kutoka maisha halisi.<br><br>Topics: Kutambua Namba 1-1000, Kujumlisha na Kutoa, Kuzidisha na Kugawanya, Vipimo, Saa na Kalenda, Pesa, Maumbo na Mfuatano."));
        library.add(Book.load("Afya na Mazingira (Kiswahili Medium Schools)", "TIE", "T419", "Tie", "Primary", "Standard 2",
            "Afya na Mazingira kwa Shule za Msingi Darasa la Pili (Kiswahili Medium Schools) hufundisha afya binafsi, lishe, usalama, na utunzaji wa mazingira.<br><br>Topics: Usafi wa Mwili, Lishe na Virutubisho, Usalama Nyumbani na Shuleni, Mwili wa Binadamu, Mimea na Wanyama, Hali ya Hewa, Maji na Taka."));
        library.add(Book.load("Utamaduni Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T420", "Tie", "Primary", "Standard 2",
            "Utamaduni Sanaa na Michezo kwa Shule za Msingi Darasa la Pili (Kiswahili Medium Schools) huendeleza uelewa wa utamaduni, sanaa, na michezo.<br><br>Topics: Nyimbo na Ngoma za Jadi, Kuchora na Kupaka Rangi, Ufundi, Hadithi, Michezo na Sanaa, Ushirikiano, Sherehe za Kitamaduni."));
        library.add(Book.load("Kusoma (Kiswahili Medium Schools)", "TIE", "T421", "Tie", "Primary", "Standard 2",
            "Kusoma kwa Shule za Msingi Darasa la Pili (Kiswahili Medium Schools) huendeleza stadi za kusoma kwa Kiswahili. Huunda ufasaha, ufahamu, na fikra makini kupitia hadithi na mazoezi.<br><br>Topics: Kusoma kwa Sauti, Ufahamu wa Kusoma, Msamiati, Sarufi, Kuandika Sentensi, Kusimulia Hadithi, Mashairi."));
        library.add(Book.load("Kuandika (Kiswahili Medium Schools)", "TIE", "T422", "Tie", "Primary", "Standard 2",
            "Kuandika kwa Shule za Msingi Darasa la Pili (Kiswahili Medium Schools) huendeleza stadi za uandishi, uundaji wa herufi na utungaji. Mazoezi ya mfululizo hujenga ujasiri wa uandishi.<br><br>Topics: Uundaji wa Herufi, Kuandika Maneno, Kuandika Sentensi, Uakifishaji, Tahajia, Uandishi wa Buni, Mazoezi ya Mwandiko."));
        library.add(Book.load("Learn English (Kiswahili Medium Schools)", "TIE", "T423", "Tie", "Primary", "Standard 2",
            "Learn English for Standard 2 (Kiswahili Medium Schools) builds English vocabulary, grammar, and communication skills. Uses songs, stories, dialogues, and fun activities.<br><br>Topics: Greetings and Introductions, Family and Friends, Food and Animals, Colours and Numbers, Classroom Objects, Simple Grammar, Reading Comprehension, Writing Sentences."));

        // ── Primary / Standard 3 Books (English Medium) ──
        library.add(Book.load("English", "TIE", "T424", "Tie", "Primary", "Standard 3",
            "English for Standard 3 develops reading, writing, speaking and listening skills. Builds vocabulary, grammar, and communication through stories, dialogues, and fun activities.<br><br>Topics: Reading Comprehension, Vocabulary Building, Grammar and Punctuation, Creative Writing, Speaking and Listening, Spelling, Sentence Structure, Poetry."));
        library.add(Book.load("Arts and Sports", "TIE", "T425", "Tie", "Primary", "Standard 3",
            "Arts and Sports for Standard 3 explores creative arts, traditional culture, and physical education. Students develop skills in drawing, music, dance, crafts, and sports.<br><br>Topics: Drawing and Painting, Traditional Music and Dance, Crafts and Sculpture, Sports and Games, Drama and Performance, Cultural Heritage, Teamwork and Leadership."));
        library.add(Book.load("Geography and the Environment", "TIE", "T426", "Tie", "Primary", "Standard 3",
            "Geography and the Environment for Standard 3 introduces physical and human geography, weather, and environmental conservation. Uses maps, diagrams, and practical activities.<br><br>Topics: Maps and Directions, Weather and Climate, Landforms, Water Bodies, Plants and Animals, Environmental Conservation, Natural Resources, Our District and Region."));
        library.add(Book.load("Mathematics", "TIE", "T427", "Tie", "Primary", "Standard 3",
            "Mathematics for Standard 3 builds on number operations, introduces multiplication and division, fractions, and problem-solving. Uses real-life examples and engaging exercises.<br><br>Topics: Whole Numbers 1-100,000, Addition and Subtraction, Multiplication and Division, Fractions, Decimals, Measurements, Geometry, Time and Calendar, Money Transactions."));
        library.add(Book.load("Science", "TIE", "T428", "Tie", "Primary", "Standard 3",
            "Science for Standard 3 explores living things, matter, energy, and the environment. Hands-on activities develop scientific curiosity and thinking skills.<br><br>Topics: Living and Non-Living Things, Human Body Systems, Plants, Animals, Matter and Materials, Energy and Light, Sound, Force and Motion, Soil, Water and Air."));

        // ── Primary / Standard 3 Books (Kiswahili Medium) ──
        library.add(Book.load("Hisabati (Kiswahili Medium Schools)", "TIE", "T429", "Tie", "Primary", "Standard 3",
            "Hisabati kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) hujenga dhana za namba, kuzidisha, kugawanya, na sehemu. Kinatumia mifano halisi na mazoezi shirikishi.<br><br>Topics: Namba 1-100,000, Kujumlisha na Kutoa, Kuzidisha na Kugawanya, Sehemu na Desimali, Vipimo, Jiometri, Saa na Kalenda, Pesa na Biashara."));
        library.add(Book.load("Jiografia na Mazingira (Kiswahili Medium Schools)", "TIE", "T430", "Tie", "Primary", "Standard 3",
            "Jiografia na Mazingira kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) hufundisha jiografia ya kimwili na ya binadamu, hali ya hewa, na uhifadhi wa mazingira.<br><br>Topics: Ramani na Maelekezo, Hali ya Hewa na Tabianchi, Maumbo ya Ardhi, Mimea na Wanyama, Uhifadhi wa Mazingira, Maliasili, Wilaya na Mkoa Wetu."));
        library.add(Book.load("Kiswahili (Kiswahili Medium Schools)", "TIE", "T431", "Tie", "Primary", "Standard 3",
            "Kiswahili kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) huendeleza stadi za lugha, kusoma, kuandika, na kuzungumza. Hukuza msamiati na ufahamu kupitia hadithi na mashairi.<br><br>Topics: Kusoma kwa Ufahamu, Msamiati na Sarufi, Kuandika Insha, Ushairi, Kusoma kwa Sauti, Mazungumzo, Matumizi ya Kamusi."));
        library.add(Book.load("Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T432", "Tie", "Primary", "Standard 3",
            "Sanaa na Michezo kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) huendeleza ubunifu, utamaduni, na elimu ya mwili. Wanafunzi hujifunza kuchora, muziki, michezo, na ufundi.<br><br>Topics: Kuchora na Kupaka Rangi, Muziki na Ngoma za Jadi, Michezo na Sanaa, Ufundi na Sanamu, Maonyesho, Utamaduni na Mila."));
        library.add(Book.load("Sayansi (Kiswahili Medium Schools)", "TIE", "T433", "Tie", "Primary", "Standard 3",
            "Sayansi kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) hufundisha viumbe hai, vitu, nishati na mazingira. Shughuli za vitendo huendeleza udadisi wa kisayansi.<br><br>Topics: Viumbe Hai na Visivyo Hai, Mwili wa Binadamu, Mimea na Wanyama, Vitu na Nyenzo, Nishati na Mwanga, Sauti, Nguvu na Mwendo."));
        library.add(Book.load("Historia ya Tanzania na Maadili (Kiswahili Medium Schools)", "TIE", "T434", "Tie", "Primary", "Standard 3",
            "Historia ya Tanzania na Maadili kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) hufundisha historia ya Tanzania, uraia, na maadili. Huunda utambulisho wa kitaifa na maadili mema.<br><br>Topics: Historia ya Tanzania, Makabila na Utamaduni, Uraia na Demokrasia, Maadili na Tabia Njema, Haki na Wajibu, Ushirikiano, Utunzaji wa Mali ya Umma."));
        library.add(Book.load("Kichina (Kiswahili Medium Schools)", "TIE", "T435", "Tie", "Primary", "Standard 3",
            "Kichina kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) huanzisha lugha ya Kichina, herufi, msamiati, na mazungumzo ya msingi. Hutumia nyimbo, picha, na mazoezi shirikishi.<br><br>Topics: Herufi na Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo, Utamaduni wa China, Sheng na Kauli."));
        library.add(Book.load("French (Kiswahili Medium Schools)", "TIE", "T436", "Tie", "Primary", "Standard 3",
            "Bonjour Les Amis! French for Standard 3 (Kiswahili Medium Schools) introduces basic French language, vocabulary, greetings, and cultural awareness. Uses songs, images, and fun activities.<br><br>Topics: Greetings and Introductions, Alphabet and Numbers, Colours and Shapes, Family and Friends, Food and Drinks, Classroom Objects, French Culture, Simple Conversations."));
        library.add(Book.load("Kiarabu (Kiswahili Medium Schools)", "TIE", "T437", "Tie", "Primary", "Standard 3",
            "Kiarabu kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) huanzisha lugha ya Kiarabu, herufi, msamiati, na mazungumzo ya msingi. Hutumia shughuli shirikishi na michezo ya lugha.<br><br>Topics: Herufi za Kiarabu, Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo, Utamaduni wa Kiarabu, Sheng na Kauli, Kuandika Maneno."));
        library.add(Book.load("Safari ya Mwili Wangu (Kiswahili Medium Schools)", "TIE", "T438", "Tie", "Primary", "Standard 3",
            "Safari ya Mwili Wangu kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) ni kitabu cha ziada cha afya na mwili wa binadamu. Huwaongoza wanafunzi katika safari ya kujifunza mwili wao.<br><br>Topics: Mwili wa Binadamu, Mifumo ya Mwili, Afya na Usafi, Lishe, Magonjwa na Kinga, Mazoezi ya Mwili, Usalama, Huduma za Afya."));
        library.add(Book.load("Mimi ni wa Thamani (Kiswahili Medium Schools)", "TIE", "T439", "Tie", "Primary", "Standard 3",
            "Mimi ni wa Thamani kwa Shule za Msingi Darasa la Tatu (Kiswahili Medium Schools) ni kitabu cha stadi za maisha na maadili. Huwajenga wanafunzi kujithamini, kujiamini, na kuwa na maadili mema.<br><br>Topics: Kujithamini na Kujiamini, Maadili na Tabia Njema, Uhusiano na Wengine, Usalama Binafsi, Afya ya Akili, Kusimamia Hisia, Malengo na Ndoto."));

        // ── Primary / Standard 4 Books (English Medium) ──
        library.add(Book.load("English", "TIE", "T448", "Tie", "Primary", "Standard 4",
            "English for Standard 4 develops reading, writing, speaking and listening skills. Builds vocabulary, grammar, and communication through stories, dialogues, and fun activities.<br><br>Topics: Reading Comprehension, Vocabulary Building, Grammar and Punctuation, Creative Writing, Speaking and Listening, Spelling, Sentence Structure, Poetry and Drama."));
        library.add(Book.load("Mathematics", "TIE", "T449", "Tie", "Primary", "Standard 4",
            "Mathematics for Standard 4 builds on number operations, multiplication, division, fractions, and geometry. Uses real-life examples and problem-solving exercises.<br><br>Topics: Whole Numbers 1-1,000,000, Addition and Subtraction, Multiplication and Division, Fractions and Decimals, Percentages, Measurements, Geometry, Time and Calendar, Money and Business."));
        library.add(Book.load("Science", "TIE", "T450", "Tie", "Primary", "Standard 4",
            "Science for Standard 4 explores living things, matter, energy, and the environment. Hands-on activities develop scientific curiosity and critical thinking.<br><br>Topics: Living Things and Environment, Human Body and Health, Plants and Growth, Animals and Classification, Matter and Materials, Energy and Motion, Light and Sound, Electricity."));
        library.add(Book.load("Geography and the Environment", "TIE", "T451", "Tie", "Primary", "Standard 4",
            "Geography and the Environment for Standard 4 introduces physical and human geography of Tanzania and the world. Uses maps, diagrams, and practical activities.<br><br>Topics: Maps and Directions, Weather and Climate, Landforms and Water Bodies, Plants and Animals, Environmental Conservation, Natural Resources, Our District and Region, Tanzania and the World."));
        library.add(Book.load("Arts and Sports", "TIE", "T452", "Tie", "Primary", "Standard 4",
            "Arts and Sports for Standard 4 explores creative arts, traditional culture, and physical education. Students develop skills in drawing, music, dance, crafts, and sports.<br><br>Topics: Drawing and Painting, Traditional Music and Dance, Crafts and Sculpture, Sports and Games, Drama and Performance, Cultural Heritage, Teamwork and Leadership, Physical Fitness."));
        library.add(Book.load("French (Kiswahili Medium Schools)", "TIE", "T453", "Tie", "Primary", "Standard 4",
            "Bonjour les Amis! French for Standard 4 (Kiswahili Medium Schools) builds French vocabulary, grammar, and communication. Uses songs, stories, dialogues, and cultural activities.<br><br>Topics: Greetings and Introductions, Numbers and Colours, Family and Friends, Food and Drinks, School Life, French Culture, Simple Conversations, Reading and Writing."));

        // ── Primary / Standard 4 Books (Kiswahili Medium) ──
        library.add(Book.load("Kiswahili (Kiswahili Medium Schools)", "TIE", "T440", "Tie", "Primary", "Standard 4",
            "Kiswahili kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) huendeleza stadi za lugha, kusoma, kuandika, na kuzungumza. Hukuza msamiati na ufahamu kupitia hadithi na mashairi.<br><br>Topics: Kusoma kwa Ufahamu, Msamiati na Sarufi, Kuandika Insha, Ushairi, Kusoma kwa Sauti, Mazungumzo, Matumizi ya Kamusi, Fasihi Simulizi."));
        library.add(Book.load("Hisabati (Kiswahili Medium Schools)", "TIE", "T441", "Tie", "Primary", "Standard 4",
            "Hisabati kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) hujenga dhana za namba, kuzidisha, kugawanya, sehemu, na jiometri. Kinatumia mifano halisi na mazoezi shirikishi.<br><br>Topics: Namba 1-1,000,000, Kujumlisha na Kutoa, Kuzidisha na Kugawanya, Sehemu na Desimali, Asilimia, Vipimo, Jiometri, Saa na Kalenda, Pesa na Biashara."));
        library.add(Book.load("Sayansi (Kiswahili Medium Schools)", "TIE", "T442", "Tie", "Primary", "Standard 4",
            "Sayansi kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) hufundisha viumbe hai, vitu, nishati, na mazingira. Shughuli za vitendo huendeleza udadisi wa kisayansi na ujuzi wa utafiti.<br><br>Topics: Viumbe Hai na Mazingira, Mwili wa Binadamu na Afya, Mimea na Ukuaji, Wanyama na Makundi, Vitu na Nyenzo, Nishati na Mwendo, Sauti na Mwanga, Umeme."));
        library.add(Book.load("Jiografia na Mazingira (Kiswahili Medium Schools)", "TIE", "T443", "Tie", "Primary", "Standard 4",
            "Jiografia na Mazingira kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) hufundisha jiografia ya Tanzania na dunia, hali ya hewa, na uhifadhi wa mazingira.<br><br>Topics: Ramani na Maelekezo, Hali ya Hewa na Tabianchi, Maumbo ya Ardhi na Maji, Mimea na Wanyama, Uhifadhi wa Mazingira, Maliasili, Wilaya na Mkoa Wetu, Tanzania na Dunia."));
        library.add(Book.load("Historia ya Tanzania na Maadili (Kiswahili Medium Schools)", "TIE", "T444", "Tie", "Primary", "Standard 4",
            "Historia ya Tanzania na Maadili kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) hufundisha historia ya Tanzania, uraia, na maadili. Huunda utambulisho wa kitaifa na maadili mema.<br><br>Topics: Historia ya Tanzania, Makabila na Tamaduni, Uraia na Demokrasia, Maadili na Tabia Njema, Haki na Wajibu, Ushirikiano na Umoja, Utunzaji wa Mali ya Umma."));
        library.add(Book.load("Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T445", "Tie", "Primary", "Standard 4",
            "Sanaa na Michezo kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) huendeleza ubunifu, utamaduni, na elimu ya mwili. Wanafunzi hujifunza kuchora, muziki, ngoma, michezo, na ufundi wa vitendo.<br><br>Topics: Kuchora na Kupaka Rangi, Muziki na Ngoma za Jadi, Michezo na Sanaa, Ufundi na Sanamu, Maonyesho na Tamthilia, Utamaduni na Mila, Mazoezi ya Mwili."));
        library.add(Book.load("Kichina (Kiswahili Medium Schools)", "TIE", "T446", "Tie", "Primary", "Standard 4",
            "Kichina kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) huanzisha lugha ya Kichina, herufi, msamiati, na mazungumzo. Hutumia nyimbo, picha, na mazoezi shirikishi.<br><br>Topics: Herufi na Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo na Utamaduni, Kusoma na Kuandika, Sheng na Kauli, Mazoezi ya Lugha."));
        library.add(Book.load("Kiarabu (Kiswahili Medium Schools)", "TIE", "T447", "Tie", "Primary", "Standard 4",
            "Kiarabu kwa Shule za Msingi Darasa la Nne (Kiswahili Medium Schools) huanzisha lugha ya Kiarabu, herufi, msamiati, na mazungumzo. Hutumia shughuli shirikishi na michezo ya lugha.<br><br>Topics: Herufi za Kiarabu, Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo, Utamaduni wa Kiarabu, Kusoma na Kuandika, Sheng na Kauli."));


        library.add(Book.load("Mathematics", "TIE", "T454", "Tie", "Primary", "Standard 5",
            "Mathematics for Standard Five builds on numeracy skills with advanced arithmetic, fractions, decimals, geometry, and problem-solving. Aligned with the new primary syllabus.<br><br>Topics: Numbers and Operations, Fractions and Decimals, Geometry and Measurement, Data Handling, Algebra Basics, Problem Solving, Time and Money, Patterns."));
        library.add(Book.load("Science", "TIE", "T455", "Tie", "Primary", "Standard 5",
            "Science for Standard Five introduces scientific inquiry, living things, matter, energy, and the environment. Hands-on activities promote critical thinking.<br><br>Topics: Scientific Investigation, Living and Non-Living Things, Human Body Systems, Plants, Matter and Energy, Light and Sound, Simple Machines, Weather and Climate."));
        library.add(Book.load("English", "TIE", "T456", "Tie", "Primary", "Standard 5",
            "English for Standard Five develops reading comprehension, writing fluency, grammar, and communication skills. Uses stories and real-life contexts.<br><br>Topics: Reading Comprehension, Grammar and Sentence Structure, Composition Writing, Vocabulary Building, Oral Communication, Poetry, Listening Skills."));
        library.add(Book.load("Geography and Environment", "TIE", "T457", "Tie", "Primary", "Standard 5",
            "Geography and Environment for Standard Five explores physical geography, maps, weather, ecosystems, and environmental conservation.<br><br>Topics: Map Reading, Physical Features, Weather and Climate, Ecosystems, Natural Resources, Environmental Conservation, Population, Settlement Patterns."));
        library.add(Book.load("Arts and Sports", "TIE", "T458", "Tie", "Primary", "Standard 5",
            "Arts and Sports for Standard Five nurtures creativity through visual arts, music, dance, and physical education. Promotes teamwork and healthy living.<br><br>Topics: Drawing and Painting, Modeling and Sculpture, Music and Rhythm, Dance and Movement, Athletics, Ball Games, Gymnastics, Sportsmanship."));
        // ── Primary / Standard 5 Books (Kiswahili Medium, New Syllabus) ──
        library.add(Book.load("Hisabati (Kiswahili Medium Schools)", "TIE", "T463", "Tie", "Primary", "Standard 5",
            "Hisabati kwa Darasa la Tano inaimarisha stadi za kuhesabu, kukokotoa, na kutatua matatizo kwa kutumia hesabu za msingi na za juu.<br><br>Topics: Namba na Shughuli za Hisabati, Sehemu na Desimali, Jiometri na Vipimo, Takwimu za Msingi, Aljebra, Utatuzi wa Matatizo, Muda na Pesa, Mfumo wa Kipimo."));
        library.add(Book.load("Sayansi (Kiswahili Medium Schools)", "TIE", "T464", "Tie", "Primary", "Standard 5",
            "Sayansi kwa Darasa la Tano inatanguliza uchunguzi wa kisayansi, viumbe hai, maada, nishati, na mazingira kwa shughuli za vitendo.<br><br>Topics: Uchunguzi wa Kisayansi, Viumbe Hai na Visivyo Hai, Mfumo wa Mwili wa Binadamu, Mimea, Maada na Nishati, Mwanga na Sauti, Mashine Rahisi, Hali ya Hewa na Tabianchi."));
        library.add(Book.load("Kiswahili (Kiswahili Medium Schools)", "TIE", "T465", "Tie", "Primary", "Standard 5",
            "Kiswahili kwa Darasa la Tano huendeleza stadi za kusoma, kuandika, kusikiliza, na kuzungumza. Hutumia nyaraka halisi na mazoezi shirikishi.<br><br>Topics: Kusoma na Kufahamu, Sarufi na Muundo wa Sentensi, Uandishi wa Insha, Msamiati, Mawasiliano ya Mdomo, Ushairi, Stadi za Kusikiliza."));
        library.add(Book.load("Jiografia na Mazingira (Kiswahili Medium Schools)", "TIE", "T466", "Tie", "Primary", "Standard 5",
            "Jiografia na Mazingira kwa Darasa la Tano inachunguza jiografia halisi, ramani, hali ya hewa, mifumo ikolojia, na uhifadhi wa mazingira.<br><br>Topics: Usomaji wa Ramani, Maumbile ya Dunia, Hali ya Hewa na Tabianchi, Mifumo Ikolojia, Maliasili, Uhifadhi wa Mazingira, Idadi ya Watu, Makazi."));
        library.add(Book.load("Historia na Maadili (Kiswahili Medium Schools)", "TIE", "T467", "Tie", "Primary", "Standard 5",
            "Historia na Maadili kwa Darasa la Tano inafundisha historia ya Tanzania, maadili ya uraia, haki za binadamu, na maamuzi ya kimaadili.<br><br>Topics: Historia ya Tanzania, Makabila na Utamaduni, Uongozi na Utawala, Haki na Wajibu, Maadili na Maamuzi, Amani na Migogoro, Ushirikishwaji wa Jamii, Utandawazi."));
        library.add(Book.load("Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T468", "Tie", "Primary", "Standard 5",
            "Sanaa na Michezo kwa Darasa la Tano inakuza ubunifu kwa sanaa za kuona, muziki, ngoma, na michezo. Inahimiza timu na maisha bora.<br><br>Topics: Uchoraji na Upakaji Rangi, Ufinyanzi na Sanamu, Muziki na Mdundo, Ngoma na Miondoko, Riadha, Michezo ya Mpira, Mazoezi ya Mwili, Ushirikiano."));
        library.add(Book.load("Kichina (Kiswahili Medium Schools)", "TIE", "T469", "Tie", "Primary", "Standard 5",
            "Kichina kwa Shule za Msingi Darasa la Tano (Kiswahili Medium Schools) huanzisha lugha ya Kichina, herufi, msamiati, na mazungumzo.<br><br>Topics: Herufi na Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo na Utamaduni, Kusoma na Kuandika, Sheng na Kauli, Mazoezi ya Lugha."));
        library.add(Book.load("Kiarabu (Kiswahili Medium Schools)", "TIE", "T470", "Tie", "Primary", "Standard 5",
            "Kiarabu kwa Shule za Msingi Darasa la Tano (Kiswahili Medium Schools) huanzisha lugha ya Kiarabu, herufi, msamiati, na mazungumzo.<br><br>Topics: Herufi za Kiarabu, Matamshi, Msamiati wa Msingi, Mazungumzo, Nyimbo, Utamaduni wa Kiarabu, Kusoma na Kuandika, Sheng na Kauli."));
        library.add(Book.load("Kifaransa (Kiswahili Medium Schools)", "TIE", "T471", "Tie", "Primary", "Standard 5",
            "Kifaransa kwa Darasa la Tano (Kiswahili Medium Schools) huanzisha lugha ya Kifaransa, msamiati, mazungumzo, na utamaduni. Hutumia shughuli shirikishi.<br><br>Topics: Msamiati wa Msingi, Mazungumzo na Matamshi, Sarufi, Kusoma na Kuandika, Utamaduni wa Kifaransa, Nyimbo, Michezo ya Kuigiza."));
        library.add(Book.load("Maisha Salama (Kiswahili Medium Schools)", "TIE", "T472", "Tie", "Primary", "Standard 5",
            "Maisha Salama kwa Darasa la Tano inafundisha usalama, afya, na stadi za kujikinga na hatari mbalimbali.<br><br>Topics: Usalama Barabarani, Usalama Nyumbani na Shuleni, Huduma ya Kwanza, Afya na Lishe, Kuzuia Ajali, Madhara ya Dawa za Kulevya, Uvuvi na Usalama Majini, Tahadhari za Dharura."));

        // ── Primary / Standard 6 Books (English Medium) ──
        library.add(Book.load("Mathematics", "TIE", "T473", "Tie", "Primary", "Standard 6",
            "Mathematics for Standard Six advances numeracy with percentages, ratios, algebra, geometry, data interpretation, and complex problem-solving.<br><br>Topics: Percentages and Ratios, Algebra and Equations, Geometry and Measurement, Data Handling and Probability, Integers, Exponents, Financial Mathematics, Logical Reasoning."));
        library.add(Book.load("Science", "TIE", "T474", "Tie", "Primary", "Standard 6",
            "Science for Standard Six deepens understanding of scientific concepts through experiments, research, and application of scientific methods.<br><br>Topics: Scientific Research Skills, Cells and Microorganisms, Human Reproduction, Nutrition and Digestion, Forces and Motion, Electricity and Magnetism, The Solar System, Environmental Science."));
        library.add(Book.load("English", "TIE", "T475", "Tie", "Primary", "Standard 6",
            "English for Standard Six enhances language proficiency through advanced reading, writing, grammar, and oral communication activities.<br><br>Topics: Advanced Reading Comprehension, Complex Grammar Structures, Essay and Report Writing, Vocabulary Expansion, Debates and Presentations, Literary Analysis, Media Literacy."));
        library.add(Book.load("Geography and Environment", "TIE", "T476", "Tie", "Primary", "Standard 6",
            "Geography and Environment for Standard Six explores world geography, climate change, resource management, and sustainable development.<br><br>Topics: World Geography, Climate and Climate Change, Water Resources and Management, Land Use and Planning, Environmental Degradation, Conservation Strategies, Sustainable Development Goals."));
        library.add(Book.load("Arts and Sports", "TIE", "T477", "Tie", "Primary", "Standard 6",
            "Arts and Sports for Standard Six develops artistic expression, cultural appreciation, and physical fitness through diverse activities.<br><br>Topics: Advanced Drawing and Painting, Sculpture and Carving, Music Composition, Drama and Performance, Athletics and Sports Techniques, Fitness and Wellness, Dance and Choreography."));
        // ── Primary / Standard 6 Books (Kiswahili Medium) ──
        library.add(Book.load("Hisabati (Kiswahili Medium Schools)", "TIE", "T482", "Tie", "Primary", "Standard 6",
            "Hisabati kwa Darasa la Sita inaingia katika asilimia, uwiano, aljebra, jiometri, tafsiri ya data, na utatuzi wa matatizo changamano.<br><br>Topics: Asilimia na Uwiano, Aljebra na Milinganyo, Jiometri na Vipimo, Takwimu na Uwezekano, Namba Kamili, Vipeo, Hisabati za Kifedha, Fikra za Kimantiki."));
        library.add(Book.load("Kiswahili (Kiswahili Medium Schools)", "TIE", "T484", "Tie", "Primary", "Standard 6",
            "Kiswahili kwa Darasa la Sita huendeleza ujuzi wa lugha kwa kusoma, kuandika, sarufi, na mawasiliano ya juu.<br><br>Topics: Ufahamu wa Juu, Sarufi na Miundo Changamano, Uandishi wa Insha na Ripoti, Msamiati, Majadiliano na Mijadala, Uchambuzi wa Fasihi, Utamaduni na Lugha."));
        library.add(Book.load("Jiografia na Mazingira (Kiswahili Medium Schools)", "TIE", "T485", "Tie", "Primary", "Standard 6",
            "Jiografia na Mazingira kwa Darasa la Sita inachunguza jiografia ya dunia, mabadiliko ya tabianchi, na maendeleo endelevu.<br><br>Topics: Jiografia ya Dunia, Tabianchi na Mabadiliko yake, Rasilimali za Maji, Matumizi ya Ardhi, Uharibifu wa Mazingira, Mikakati ya Uhifadhi, Malengo ya Maendeleo Endelevu."));
        library.add(Book.load("Historia na Maadili (Kiswahili Medium Schools)", "TIE", "T486", "Tie", "Primary", "Standard 6",
            "Historia na Maadili kwa Darasa la Sita inaangalia historia ya Afrika na dunia, mifumo ya utawala, na maadili ya uongozi.<br><br>Topics: Historia ya Afrika, Mfumo wa Utawala wa Kikoloni, Harakati za Uhuru, Uongozi na Maadili, Katiba na Sheria, Demokrasia na Uchaguzi, Haki za Binadamu, Amani na Usalama."));
        library.add(Book.load("Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T487", "Tie", "Primary", "Standard 6",
            "Sanaa na Michezo kwa Darasa la Sita inakuza ubunifu wa kisanaa, uthamini wa utamaduni, na siha ya mwili.<br><br>Topics: Uchoraji na Upakaji Rangi, Sanamu na Uchongaji, Utungaji wa Muziki, Mchezo wa Kuigiza, Riadha na Michezo, Siha na Sifa, Ngoma na Choreografia, Utamaduni wa Sanaa."));
        library.add(Book.load("Elimu ya Afya ya Uzazi na Ustawi (Kiswahili Medium Schools)", "TIE", "T488", "Tie", "Primary", "Standard 6",
            "Elimu ya Afya ya Uzazi na Ustawi kwa Darasa la Sita inafundisha afya ya uzazi, ustawi wa jamii, na stadi za maisha.<br><br>Topics: Afya ya Uzazi, Mabadiliko ya Balehe, Mahusiano na Mawasiliano, Ustawi wa Jamii, Kuzuia Mimba za Utotoni, VVU na UKIMWI, Madawa ya Kulevya, Stadi za Kujitetea."));
        library.add(Book.load("Maisha Salama (Kiswahili Medium Schools)", "TIE", "T489", "Tie", "Primary", "Standard 6",
            "Maisha Salama kwa Darasa la Sita inaendeleza elimu ya usalama, afya, na kujikinga na hatari katika mazingira mbalimbali.<br><br>Topics: Usalama Barabarani na Usafiri, Usalama Mtandaoni, Afya na Lishe Bora, Huduma ya Kwanza ya Juu, Kuzuia Magonjwa, Majanga ya Asili na Kukabiliana Nayo, Stadi za Kuishi Salama, Uhamasishaji wa Jamii."));

        // ── Primary / Standard 7 Books (English Medium) ──
        library.add(Book.load("Mathematics", "TIE", "T490", "Tie", "Primary", "Standard 7",
            "Mathematics for Standard Seven prepares students for secondary school with comprehensive coverage of all primary-level mathematics topics.<br><br>Topics: Number Systems and Operations, Fractions, Decimals and Percentages, Algebra and Equations, Geometry and Mensuration, Data Handling and Probability, Ratio and Proportion, Financial Literacy."));
        library.add(Book.load("Science", "TIE", "T491", "Tie", "Primary", "Standard 7",
            "Science for Standard Seven consolidates scientific knowledge and prepares learners for secondary science through integrated investigations.<br><br>Topics: Integrated Science Skills, Heredity and Evolution, Human Body Systems, Ecosystems and Food Chains, Energy Transformations, Chemical Reactions, Space Exploration, Environmental Challenges and Solutions."));
        library.add(Book.load("English", "TIE", "T492", "Tie", "Primary", "Standard 7",
            "English for Standard Seven prepares students for secondary English with advanced literacy, critical thinking, and communication skills.<br><br>Topics: Critical Reading and Analysis, Advanced Composition, Research and Report Writing, Public Speaking and Presentations, Grammar Mastery, Creative Writing, Media and Digital Literacy, Literature Appreciation."));
        library.add(Book.load("Geography and Environment", "TIE", "T493", "Tie", "Primary", "Standard 7",
            "Geography and Environment for Standard Seven integrates physical and human geography with environmental management and global issues.<br><br>Topics: Physical Geography Review, Human Geography, Urbanization and Settlement, Environmental Management, Global Environmental Issues, Climate Action, Biodiversity Conservation, Sustainable Resource Use."));
        library.add(Book.load("Arts and Sports", "TIE", "T494", "Tie", "Primary", "Standard 7",
            "Arts and Sports for Standard Seven combines creative arts with advanced sports skills, preparing students for lifelong physical activity.<br><br>Topics: Creative Arts Portfolio, Advanced Music and Performance, Sports Leadership, Team Sports Strategies, Health and Fitness Planning, Cultural Festivals and Events, Art Exhibition, Sports Officiating."));

        // ── Primary / Standard 7 Books (Kiswahili Medium) ──
        library.add(Book.load("Hisabati (Kiswahili Medium Schools)", "TIE", "T499", "Tie", "Primary", "Standard 7",
            "Hisabati kwa Darasa la Saba inamwandaa mwanafunzi kwa elimu ya sekondari kwa maelezo kamili ya hisabati za msingi.<br><br>Topics: Mifumo ya Namba na Shughuli, Sehemu na Asilimia, Aljebra na Milinganyo, Jiometri na Upimaji, Takwimu na Uwezekano, Uwiano na Sawia, Elimu ya Fedha."));
        library.add(Book.load("Kiswahili (Kiswahili Medium Schools)", "TIE", "T501", "Tie", "Primary", "Standard 7",
            "Kiswahili kwa Darasa la Saba kinamwandaa mwanafunzi kwa Kiswahili cha sekondari kwa stadi za juu za lugha.<br><br>Topics: Usomaji Makini na Uchambuzi, Uandishi wa Juu, Utafiti na Uandishi wa Ripoti, Hotuba na Mawasilisho, Sarufi Kamili, Uandishi wa Bunilizi, Fasihi na Uchambuzi Wake, Lugha na Teknolojia."));
        library.add(Book.load("Jiografia na Mazingira (Kiswahili Medium Schools)", "TIE", "T502", "Tie", "Primary", "Standard 7",
            "Jiografia na Mazingira kwa Darasa la Saba inachanganya jiografia halisi na ya binadamu na usimamizi wa mazingira.<br><br>Topics: Mapitio ya Jiografia Halisi, Jiografia ya Binadamu, Miji na Makazi, Usimamizi wa Mazingira, Masuala ya Mazingira Duniani, Hatua za Tabianchi, Uhifadhi wa Bioanuai, Matumizi Endelevu ya Rasilimali."));
        library.add(Book.load("Historia na Maadili (Kiswahili Medium Schools)", "TIE", "T503", "Tie", "Primary", "Standard 7",
            "Historia na Maadili kwa Darasa la Saba inamwandaa mwanafunzi kwa historia na maadili ya sekondari.<br><br>Topics: Historia ya Tanzania na Afrika, Mchange wa Utawala, Harakati za Ukombozi, Ujenzi wa Taifa, Maadili ya Uongozi, Haki na Usawa, Demokrasia na Vyama Vingi, Tanzania katika Jumuiya za Kimataifa."));
        library.add(Book.load("Sanaa na Michezo (Kiswahili Medium Schools)", "TIE", "T504", "Tie", "Primary", "Standard 7",
            "Sanaa na Michezo kwa Darasa la Saba inachanganya sanaa bunifu na michezo ya juu kumwandaa mwanafunzi kwa maisha.<br><br>Topics: Kwingineo la Sanaa Bunifu, Muziki wa Juu na Maonyesho, Uongozi wa Michezo, Mikakati ya Michezo ya Timu, Mipango ya Afya na Siha, Sherehe za Utamaduni, Maonyesho ya Sanaa, Uamuzi wa Michezo."));
        library.add(Book.load("Elimu ya Afya ya Uzazi na Ustawi (Kiswahili Medium Schools)", "TIE", "T505", "Tie", "Primary", "Standard 7",
            "Elimu ya Afya ya Uzazi na Ustawi kwa Darasa la Saba inatoa elimu ya juu ya afya ya uzazi na ustawi kwa maisha bora.<br><br>Topics: Afya ya Uzazi na Ustawi, Uhusiano na Mawasiliano, Maamuzi ya Maisha, Kuzuia Unyanyasaji, Afya ya Akili, Ustawi wa Jamii, Uwezeshaji wa Kiuchumi, Maadili ya Teknolojia."));
        library.add(Book.load("Maisha Salama (Kiswahili Medium Schools)", "TIE", "T506", "Tie", "Primary", "Standard 7",
            "Maisha Salama kwa Darasa la Saba inajenga utamaduni wa usalama na kuwajibika kwa mwanafunzi anayekabiliana na changamoto mbalimbali.<br><br>Topics: Usalama na Usafiri, Usalama Dijitali na Mtandaoni, Afya na Usafi wa Mazingira, Uzuiaji wa Magonjwa ya Kuambukiza, Mabadiliko ya Tabianchi na Athari Zake, Usalama wa Chakula na Maji, Huduma ya Kwanza na Dharura, Ushirikishwaji wa Jamii."));

        // ── Flipbook URLs (TIE self-hosted, fliphtml5 fallback) ──
        flipbooks.put("T009", "https://online.fliphtml5.com/ebxst/ohok/");
        flipbooks.put("T012", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Biology/Biology_F5.html");
        flipbooks.put("T013", "https://online.fliphtml5.com/ebxst/qdgm/");
        flipbooks.put("T014", "https://online.fliphtml5.com/ebxst/tmhq/");
        flipbooks.put("T016", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Kiswahili%20Shule%20za%20Sekondari%20Kiongozi%20cha%20Mwalimu%20Kidato%20cha%20Tano/SB/Kiswahili_Form_5.html");
        flipbooks.put("T019", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Accountancy/Accountancy_F5.html");
        flipbooks.put("T022", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Computer_Sci/Computer_Science_F5.html");
        flipbooks.put("T023", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/English/English_F5.html");
        flipbooks.put("T024", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Academic_Comms/Acedemic_Comms_F5.html");
        flipbooks.put("T025", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/General_n_Inorganic_Chenistry/Gen_n_Inorganic_Chemistry_Form_5.html");
        flipbooks.put("T026", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Organic_Chemistry/Organic_Chem_Form_5.html");
        flipbooks.put("T027", "https://online.fliphtml5.com/ebxst/iosv/");
        flipbooks.put("T028", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/LITERATURE%20IN%20ENGLISH%20FORM%205/SB/Literature_English_Form_5.html");
        flipbooks.put("T029", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Business_Studies/Business_Studies_F5.html");
        flipbooks.put("T030", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Chinese/Chinese_F5.html");
        flipbooks.put("T031", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Divinity/Divinity_F5.html");
        flipbooks.put("T032", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/EDK/EDK_F5.html");
        flipbooks.put("T033", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Fasihi_Kisw/Fasihi_Kisw_F5.html");
        flipbooks.put("T034", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Arabic/Arabic_F5.html");
        flipbooks.put("T035", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/FOOD%20n%20HUMAN%20NUTRITION%20F5/SB/Food_Form_5.html");
        flipbooks.put("T036", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/THEATRE%20ART%20F5%20%2828%EF%80%A206%EF%80%A22024%29%20fnl_/SB/Theatre_Arts_Form_5.html");
        flipbooks.put("T037", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/MUSIC%20FORM%20FIVe/SB/Music_Form_5.html");
        flipbooks.put("T038", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/TOURISM/Tourisn_Form_5.html");
        flipbooks.put("T039", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Agriculture/index.html");
        flipbooks.put("T040", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Physical_Chemistry/Physical_Chem_Form_5.html");
        flipbooks.put("T041", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Historia_Maadili/Historia_ya_Tz_na_Maadili_Form_5.html");
        flipbooks.put("T042", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Microeconomics%20for%20Advanced%20Secondary%20Schools%20Teachers%20Guide%20Form%20Five/SB/Microeconomic_Form_5.html");
        flipbooks.put("T043", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/French/French_Form_5.html");
        flipbooks.put("T044", "https://online.fliphtml5.com/ebxst/lxeg/");
        flipbooks.put("T100", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Academic_Comms/Academic_Comms_Form_6.html");
        flipbooks.put("T101", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Accountancy/Accountancy_Form_6.html");
        flipbooks.put("T102", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Agriculture_New/Agriculture_Form_6.html");
        flipbooks.put("T103", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Arabic/Arabic_Form_6.html");
        flipbooks.put("T104", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Biology/Biology_Form_6.html");
        flipbooks.put("T105", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Biz_Studies/Biz_Studies_Form_6.html");
        flipbooks.put("T106", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Chinese/Chinese_Form_6.html");
        flipbooks.put("T107", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Computer_Sci/Computer_Sci_Form_6.html");
        flipbooks.put("T108", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Divinity/Divinity_Form_6.html");
        flipbooks.put("T109", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/English/English_Form_6.html");
        flipbooks.put("T110", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Fasihi_Kiswahili/Fasihi_Kiswahili_Form_6.html");
        flipbooks.put("T111", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Fine_Art/Fine_Art_Form_6.html");
        flipbooks.put("T112", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Food_n_Human/Food_n_Human_Form_6.html");
        flipbooks.put("T113", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/French/French_Form_6.html");
        flipbooks.put("T114", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Hist_TZ_Maadili/Hist_TZ_Maadili_Form_6.html");
        flipbooks.put("T115", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/History/History_Form_6.html");
        flipbooks.put("T116", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Kiswahili/Kiswahili_Form_6.html");
        flipbooks.put("T117", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Literature_in_Eng/Literature_in_Eng_Form_1.html");
        flipbooks.put("T118", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Macroeconomics/Macroeconomics_Form_1.html");
        flipbooks.put("T119", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Mathematics/Mathematics_Form_1.html");
        flipbooks.put("T120", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Music/Music_Form_1.html");
        flipbooks.put("T121", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Physics/Physics_Form_1.html");
        flipbooks.put("T122", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Sports_Studies/Sports_Studies_Form_1.html");
        flipbooks.put("T123", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Textile/Textile_Form_1.html");
        flipbooks.put("T124", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Theatre_Arts/Theatre_Arts_Form_6.html");
        flipbooks.put("T125", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmvi/Tourism/Tourism_Form_6.html");

        // ── Form 4 flipbook URLs (TIE self-hosted) ──
        flipbooks.put("T045", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Basic_Maths/Basic_Maths_F4.html");
        flipbooks.put("T046", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Biology/Biology_F4.html");
        flipbooks.put("T047", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Chemistry/Chemistry_Form_4.html");
        flipbooks.put("T048", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Physics/Physics_Form_4.html");
        flipbooks.put("T049", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/History/History_Form_4.html");
        flipbooks.put("T050", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Geography/Geography_Form_4.html");
        flipbooks.put("T051", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/English/English_Form_4.html");
        flipbooks.put("T052", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Kiswahili/Kiswahili_Form_4.html");
        flipbooks.put("T053", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Civics/Civics_F4.html");
        flipbooks.put("T054", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/ICS/ICS_Form_4.html");
        flipbooks.put("T055", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Bookeeping/Bookeeping_F4.html");
        flipbooks.put("T056", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Commerce/Commerce_Form_1.html");
        flipbooks.put("T057", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Agriculture/Agric_F4.html");

        // ── Form 3 flipbook URLs (TIE self-hosted) ──
        flipbooks.put("T058", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Basic_Maths/Basic_Maths_F3.html");
        flipbooks.put("T059", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Chemistry/Chemistry_Form3.html");
        flipbooks.put("T060", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Physics/Physics_Form3.html");
        flipbooks.put("T061", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Biology/Biology_Form3.html");
        flipbooks.put("T062", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/History/History_Form3.html");
        flipbooks.put("T063", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Geography/Geography_Form3.html");
        flipbooks.put("T064", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/English/English_Form3.html");
        flipbooks.put("T065", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Kiswahili/Kiswahili_Form3.html");
        flipbooks.put("T066", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Civics/Civics_Form3.html");
        flipbooks.put("T067", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/ICS/ICS_Form3.html");
        flipbooks.put("T068", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Bookeeping/Bookeeping_Form3.html");
        flipbooks.put("T069", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Commerce/Commerce_Form3.html");
        flipbooks.put("T070", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Agriculture/2025/Agriculture_Form_3.html");

        // ── Form 3 Extra flipbook URLs ──
        flipbooks.put("T071", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Literature/Literature_Form3.html");
        flipbooks.put("T072", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/FrenchSB/2025/French_Form_3.html");
        flipbooks.put("T073", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Chinese/Chimese_Form_3.html");
        flipbooks.put("T074", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/arabicSB/2025/Arabic_Form_3.html");
        flipbooks.put("T075", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/HistoriayaSB/2025/Historia_Maadili_Form_3.html");
        flipbooks.put("T076", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Food_N_Human/Food_and_Human_Nurt_Form3.html");
        flipbooks.put("T077", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Textile_n_Garment/Textile_and_Garment_Form3.html");
        flipbooks.put("T078", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Three/Business Studies/2025/Bisiness Studies_Form_3.html");

        // ── More Form 4 flipbook URLs ──
        flipbooks.put("T079", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Food&Human/Food&Human_Form_4.html");
        flipbooks.put("T080", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Four/Textile_n_Garment/Textiles and Garment Construction F4.html");

        // ── Form 2 flipbook URLs (TIE self-hosted) ──
        flipbooks.put("T081", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Mathematics_Form_Two/Mathematics_Form_Two.html");
        flipbooks.put("T082", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Biology_Form_Two/Biology Form Two.html");
        flipbooks.put("T083", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Chemistry/Chemistry_Form_2.html");
        flipbooks.put("T084", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Physics_Form_Two/Physics_Form_2.html");
        flipbooks.put("T085", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/geography/Geography Form Two.html");
        flipbooks.put("T086", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/English_Form_Two/UPDATED/English_Form_Two.html");
        flipbooks.put("T087", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/kiswahili/fp/index.html");
        flipbooks.put("T088", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/bookkeeping/fp/index.html");
        flipbooks.put("T089", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Agriculture_Form_Two/Agriculture Form Two.html");
        flipbooks.put("T090", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/food/fp/index.html");
        flipbooks.put("T091", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/textiles F2/fp/index.html");
        flipbooks.put("T092", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/computer/fp/index.html");
        flipbooks.put("T093", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Chinese_Form_Two/CHINESE.html");
        flipbooks.put("T094", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Arabic_Form_Two/Arabic Form Two.html");
        flipbooks.put("T095", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/French_Form_Two/French_Form_Two.html");
        flipbooks.put("T096", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Businesss_Studies_Form_Two/Business Studies.html");
        flipbooks.put("T097", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/music/fp/index.html");
        flipbooks.put("T098", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/sports/fp/index.html");
        flipbooks.put("T099", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Theatre_Arts/Theatre_Arts.html");
        flipbooks.put("T150", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/fine/fp/index.html");

        // ── Form 2 Vocational flipbook URLs ──
        flipbooks.put("T200", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/weldingMetal/fp/index_7.html");
        flipbooks.put("T201", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/t_events/fp/index_7.html");
        flipbooks.put("T202", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/t_guide/fp/index_7.html");
        flipbooks.put("T203", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/techdrawing/fp/index_7.html");
        flipbooks.put("T204", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/solar/fp/index_7.html");
        flipbooks.put("T205", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/foodbeverage/index_7.html");
        flipbooks.put("T206", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/field_crop/fp/index_7.html");
        flipbooks.put("T207", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/electr_repair/fp/index_7.html");
        flipbooks.put("T208", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/elecInstall/fp/index_7.html");
        flipbooks.put("T209", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/compappl/fp/index_7.html");
        flipbooks.put("T210", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/comp_prog/fp/index_7.html");
        flipbooks.put("T211", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/civi/fp/index_7.html");
        flipbooks.put("T212", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/refridg/fp/index_7.html");
        flipbooks.put("T213", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/plumbing/fp/index_7.html");
        flipbooks.put("T214", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/mus_soun/fp/index_7.html");
        flipbooks.put("T215", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/art_des/fp/index_7.html");
        flipbooks.put("T216", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/carpentry/index_7.html");
        flipbooks.put("T217", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/form_two/agro_mecsf2/index_7.html");
        flipbooks.put("T218", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_Two/Historia_Maadili/Historia_Maadili.html");
        flipbooks.put("T219", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Fine_Art_Form_One/Fine Arts.html");
        flipbooks.put("T220", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/French_Form_One/French_Form_1.html");
        flipbooks.put("T221", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/music_sound/fp/index_7.html");
        flipbooks.put("T222", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/m_cycle/fp/index_7.html");
        flipbooks.put("T223", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/netbal/fp/index_7.html");
        flipbooks.put("T224", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/music_perfomance/fp/index_7.html");
        flipbooks.put("T225", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/wood_processing/fp/index_7.html");
        flipbooks.put("T226", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/welding_metal/fp/index_7.html");
        flipbooks.put("T227", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/track_events/fp/index_7.html");
        flipbooks.put("T228", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/tour_guide/fp/index_7.html");
        flipbooks.put("T229", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/technical_drawing/fp/index_7.html");
        flipbooks.put("T230", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/solar/fp/index_7.html");
        flipbooks.put("T231", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/refridgeration/fp/index_7.html");
        flipbooks.put("T232", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/plumbing_pipe/fp/index_7.html");
        flipbooks.put("T233", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/painting_signwriter/fp/index_7.html");
        flipbooks.put("T234", "https://ol.tie.go.tz/uploaded_files/books//secondary/Vocational/Form_One/ngoma/fp/index_7.html");
        flipbooks.put("T235", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Bible_Knowledge/BibleKnowledgeFormOne.html");
        flipbooks.put("T236", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/EDK/EDK_Form_One.html");
        flipbooks.put("T237", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Hist_Maadili_Tz/Hist_Maadili_Tz.html");
        flipbooks.put("T238", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Chemistry_Form_One/Chemistry_Form_One.html");
        flipbooks.put("T239", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/History/History_Form_One.html");
        flipbooks.put("T240", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Agriculture/Agriculture_Form_One.html");
        flipbooks.put("T241", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Thiatre_Arts_Form_One/Version_2/Thiatre_ Arts.html");
        flipbooks.put("T242", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Textile_and_Garment_Form_One/Textile.html");
        flipbooks.put("T243", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Sports_Studies_Form_One/Sports_Studies_Form_1.html");
        flipbooks.put("T244", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Physics_Form_One/Physics.html");
        flipbooks.put("T245", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Music_Form_One/Music.html");
        flipbooks.put("T246", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Mathematics_Form_One/Mathematics.html");
        flipbooks.put("T247", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Geography_Form_One/Geography for Secondary Schools Student\u2019s Book Form One.html");
        flipbooks.put("T248", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Food_Human_Nurt_Form_One/Food And Human.html");
        flipbooks.put("T249", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/English_Form_One/English.html");
        flipbooks.put("T250", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Computer_Science_Form_One/Computer.html");
        flipbooks.put("T251", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Chinese_Form_One/Chinese.html");
        flipbooks.put("T252", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Business_Studies_Form_One/Bisiness Studies.html");
        flipbooks.put("T253", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Book-keeping_Form_One/Book-keeping for Secondary Schools Student\u2019s Book Form One.html");
        flipbooks.put("T254", "https://ol.tie.go.tz/uploaded_files/books//secondary/Form_One/Biology_Form_One/Biology_Form_One.html");

        // ── Pre-Primary / Nursery flipbook URLs (fliphtml5) ──
        flipbooks.put("T310", "https://online.fliphtml5.com/rwbnv/eoen/");
        flipbooks.put("T311", "https://online.fliphtml5.com/rwbnv/vjku");
        flipbooks.put("T312", "https://online.fliphtml5.com/rwbnv/wypa");
        flipbooks.put("T313", "https://online.fliphtml5.com/rwbnv/llak");
        flipbooks.put("T314", "https://online.fliphtml5.com/rwbnv/vdgc");
        flipbooks.put("T315", "https://online.fliphtml5.com/rwbnv/plcv");

        // ── Primary / Standard 1 flipbook URLs ──
        flipbooks.put("T400", "https://online.fliphtml5.com/rwbnv/fvcv");
        flipbooks.put("T401", "https://online.fliphtml5.com/rwbnv/ondo");
        flipbooks.put("T402", "https://online.fliphtml5.com/rwbnv/dufp");
        flipbooks.put("T403", "https://online.fliphtml5.com/rwbnv/zvcm");
        flipbooks.put("T404", "https://online.fliphtml5.com/rwbnv/imme");
        flipbooks.put("T405", "https://online.fliphtml5.com/rwbnv/brfw");
        flipbooks.put("T406", "https://online.fliphtml5.com/rwbnv/uyum");
        flipbooks.put("T407", "https://online.fliphtml5.com/rwbnv/lctl");
        flipbooks.put("T408", "https://online.fliphtml5.com/rwbnv/egcu");
        flipbooks.put("T409", "https://online.fliphtml5.com/rwbnv/jisk");
        flipbooks.put("T410", "https://online.fliphtml5.com/rwbnv/zlvn");
        flipbooks.put("T411", "https://online.fliphtml5.com/rwbnv/pncn");

        // ── Primary / Standard 2 flipbook URLs ──
        flipbooks.put("T412", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Aritmetic/Arithmetic_Std_One.html");
        flipbooks.put("T413", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Health_n_Envir/Health_and_Environment.html");
        flipbooks.put("T414", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Culture_Art_n_Sports/Cult_Art_Sport_Std_One.html");
        flipbooks.put("T415", "https://ol.tie.go.tz/uploaded_files/books//primary/std2/Kusoma%20Eng%20Med/Kusoma%20English%20Medium%20DRS%202.html");
        flipbooks.put("T416", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Learn_English/Learn_English.html");
        flipbooks.put("T417", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Writing/Writing.html");
        flipbooks.put("T418", "https://ol.tie.go.tz/uploaded_files/books//primary/std2/Kuhesabu/Kuhesabu%20DRS%202.html");
        flipbooks.put("T419", "https://ol.tie.go.tz/uploaded_files/books//primary/std4/Sw/Afya_Mazingira/Afta_Mazingira_Std_2.html");
        flipbooks.put("T420", "https://ol.tie.go.tz/uploaded_files/books//primary/std2/Utamaduni_na_Sanaa/Utamaduni%20Sanaa%20Michezo%20DRS%202.html");
        flipbooks.put("T421", "https://ol.tie.go.tz/uploaded_files/books//primary/std2/Kusoma/Kusoma%20DRS%20II.html");
        flipbooks.put("T422", "https://ol.tie.go.tz/uploaded_files/books//primary/std2/Kuandika/Kuandika_DRS_2.html");
        flipbooks.put("T423", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std2/Learn_Eng_Sw/Learn_Eng_Sw.html");

        // ── Primary / Standard 3 flipbook URLs ──
        flipbooks.put("T424", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/english/flip/Index.html");
        flipbooks.put("T425", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/ARTS%20AND%20SPORTS/FLIP/flipbook/index.html");
        flipbooks.put("T426", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/GEAGRAPHY/FLIP/flipbook/index.html");
        flipbooks.put("T427", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/MATHEMATICS/Updated/Maths_Std_3.html");
        flipbooks.put("T428", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/SCIENCE/FLIP/flipbook/index.html");
        flipbooks.put("T429", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/HISABATI/FLIP/flipbook/index.html");
        flipbooks.put("T430", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/JIOGRAFIA%20NA%20MAZINGIRA/FLIP/flipbook/index.html");
        flipbooks.put("T431", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/KISWAHILI/FLIP/index.html");
        flipbooks.put("T432", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/SANAA%20NA%20MICHEZO/FLIP/flipbook/index.html");
        flipbooks.put("T433", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/SAYANSI/FLIP/flipbook/index.html");
        flipbooks.put("T434", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/HISTORIA%20YA%20TZ/FLIP/flipbook/index.html");
        flipbooks.put("T435", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/CHINESE/FLIP/flipbook/index.html");
        flipbooks.put("T436", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/FRENCH/FLIP/flipbook/index.html");
        flipbooks.put("T437", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/KIARABU/FLIP/flipbook/index.html");
        flipbooks.put("T438", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/Safari%20ya%20Mwili%20Wangu/Safari%20ya%20Mwili%20wangu.html");
        flipbooks.put("T439", "https://ol.tie.go.tz/uploaded_files/books//primary/std3/Mimi%20ni%20wa%20Thamani/Mimi%20ni%20wa%20Thamani.html");
        flipbooks.put("T440", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Kiswahili/Kiswahili_Std_4.html");
        flipbooks.put("T441", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Hisabati/Hisabati_Std_4.html");
        flipbooks.put("T442", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Sayansi_Drs_4/Sayansi_Drs_4.html");
        flipbooks.put("T443", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Jiogefia_Mazingira/Jiografia_Mazingira.html");
        flipbooks.put("T444", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Hist_Maadili/Historiayatznamaadili.html");
        flipbooks.put("T445", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Sanaa_Michezo/Sanaa_Michezo_Drs_4.html");
        flipbooks.put("T446", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Kichina/Kichina_Drs_4.html");
        flipbooks.put("T447", "https://ol.tie.go.tz/uploaded_files/books//primary/std4/Sw/Kiarabu/Kiarabu_Drs_4.html");
        flipbooks.put("T448", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/English/English_Std_4.html");
        flipbooks.put("T449", "https://ol.tie.go.tz/uploaded_files/books//primary/std4/Eng/Mathematics/Mathematics_Std_4.html");
        flipbooks.put("T450", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Science/Science_Std_4.html");
        flipbooks.put("T451", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Geography/Geography_n_Environ_Std_4.html");
        flipbooks.put("T452", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/Art_n_Sports/Art_n_Sports_Std_4.html");
        flipbooks.put("T453", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std4/French/French_Std_4.html");

        // ── Standard 5 Flipbook URLs ──
        flipbooks.put("T454", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std5/mathematics/mathematics_std_5.html");
        flipbooks.put("T455", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std5/Science/Science_std5.html");
        flipbooks.put("T456", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std5/English/English_Std_5.html");
        flipbooks.put("T457", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std5/Geography/Geography_Std_5.html");
        flipbooks.put("T458", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std5/Arts_and_Sports/Arts_and_Sports_Std_5.html");
        flipbooks.put("T463", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Hisabati/Hisabati_Std_5.html");
        flipbooks.put("T464", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Sayansi/Sayansi_Std_5.html");
        flipbooks.put("T465", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Kiswahili/Kiswahili_Std_5.html");
        flipbooks.put("T466", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Jiografia/Jiografia_Std_5.html");
        flipbooks.put("T467", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Historia_na_Maadili/Historia_na_Maadili_Std_5.html");
        flipbooks.put("T468", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Sanaa_na_Michezo/Sanaa_na_Michezo_Std_5.html");
        flipbooks.put("T469", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Chinese/Chinese_Std_5.html");
        flipbooks.put("T470", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Arabic/Arabic_Std_5.html");
        flipbooks.put("T471", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/French/French_Std_5.html");
        flipbooks.put("T472", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std5/Maisha_Salama/Maisha_Salama_Std_5.html");

        // ── Standard 6 Flipbook URLs ──
        flipbooks.put("T473", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std6/Mathematics/Mathematics_Std_6.html");
        flipbooks.put("T474", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std6/Science/Science_Std_6.html");
        flipbooks.put("T475", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std6/English/English_Std_6.html");
        flipbooks.put("T476", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std6/Geography/Geography_Std_6.html");
        flipbooks.put("T477", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std6/Arts_and_Sports/Arts_n_Sports_Std_6.html");
        flipbooks.put("T484", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Kiswahili/Kiswahili_Std_6.html");
        flipbooks.put("T485", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Jiografia_na_Mazingira/Jiografia_na_Mazingira_Std_6.html");
        flipbooks.put("T486", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Historia_na_Maadili/Historia_na_Maadili_Std_6.html");
        flipbooks.put("T487", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Sanaa_na_Michezo/Sanaa_na_Michezo_Std_6.html");
        flipbooks.put("T488", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Elimu_ya_Afya_ya_Uzazi/Elimu_ya_Afya_Uzazi_Std_6.html");
        flipbooks.put("T489", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std6/Maisha_Salama/Maisha_Salama_Std_6.html");

        // ── Standard 7 Flipbook URLs ──
        flipbooks.put("T490", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std7/Mathematics/mathematics_std_7.html");
        flipbooks.put("T491", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std7/Science/Science_Std_7.html");
        flipbooks.put("T492", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std7/English/English_Std_7.html");
        flipbooks.put("T493", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std7/Geography/Geography_Std_7.html");
        flipbooks.put("T494", "https://ol.tie.go.tz/uploaded_files/books//primary/Eng/Std7/Arts_and_Sports/Arts_and_Sports_Std_7.html");
        flipbooks.put("T501", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Kiswahili/Kiswahili_Std_7.html");
        flipbooks.put("T502", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Jiografia_na_Mazingira/Jiografia_na_Mazingira_Std_7.html");
        flipbooks.put("T503", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Historia_na_Maadili/Historia_na_Maadili_Std_7.html");
        flipbooks.put("T504", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Sanaa_na_Michezo/Sanaa_na_Michezo_Std_7.html");
        flipbooks.put("T505", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Elimu_ya_Afya_ya_Uzazi/Elimu_ya_Afya_Uzazi_Std_7.html");
        flipbooks.put("T506", "https://ol.tie.go.tz/uploaded_files/books//primary/Kisw/Std7/Maisha_Salama/Maisha_Salama_Std_7.html");

        users.add(new User("admin", "admin123", "admin"));
        loadUsers();
        loadRequests();
        loadBorrows();

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7860"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> { try { handleRoot(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/signup", exchange -> { try { handleSignup(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/login", exchange -> { try { handleLogin(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/logout", exchange -> { try { handleLogout(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/dashboard", exchange -> { try { handleDashboard(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/admin", exchange -> { try { handleAdmin(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/read", exchange -> { try { handleRead(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/forgot-password", exchange -> { try { handleForgotPassword(exchange); } catch (Exception e) { e.printStackTrace(); }});

        System.out.println("Server: http://localhost:" + port);
        System.out.println("LAN:    http://" + java.net.InetAddress.getLocalHost().getHostAddress() + ":" + port);
        server.setExecutor(null);
        server.start();
    }

    // ── User persistence ──────────────────────────────────

    private static void saveUsers() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(DATA_FILE))) {
                oos.writeObject(users);
            }
        } catch (Exception e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(DATA_FILE))) {
                List<User> saved = (List<User>) ois.readObject();
                for (User u : saved) {
                    if (users.stream().noneMatch(ex -> ex.getUsername().equals(u.getUsername())))
                        users.add(u);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    // ── Requests / Borrows persistence ─────────────────────

    @SuppressWarnings("unchecked")
    private static void loadRequests() {
        if (!Files.exists(REQUESTS_FILE)) return;
        try {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(REQUESTS_FILE))) {
                List<Request> saved = (List<Request>) ois.readObject();
                requests.addAll(saved);
            }
        } catch (Exception e) {
            System.err.println("Failed to load requests: " + e.getMessage());
        }
    }

    private static void saveRequests() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(REQUESTS_FILE))) {
                oos.writeObject(requests);
            }
        } catch (Exception e) {
            System.err.println("Failed to save requests: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadBorrows() {
        if (!Files.exists(BORROWS_FILE)) return;
        try {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(BORROWS_FILE))) {
                List<BorrowRecord> saved = (List<BorrowRecord>) ois.readObject();
                borrows.addAll(saved);
            }
        } catch (Exception e) {
            System.err.println("Failed to load borrows: " + e.getMessage());
        }
    }

    private static void saveBorrows() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(BORROWS_FILE))) {
                oos.writeObject(borrows);
            }
        } catch (Exception e) {
            System.err.println("Failed to save borrows: " + e.getMessage());
        }
    }

    // ── Session helpers ────────────────────────────────────

    private static String getSessionUser(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie == null) return null;
        for (String c : cookie.split(";")) {
            String[] kv = c.trim().split("=", 2);
            if (kv.length == 2 && "sid".equals(kv[0]))
                return sessions.get(kv[1]);
        }
        return null;
    }

    private static void setSession(HttpExchange exchange, String username) {
        String sid = UUID.randomUUID().toString();
        sessions.put(sid, username);
        exchange.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; Path=/");
    }

    private static void clearSession(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null) {
            for (String c : cookie.split(";")) {
                String[] kv = c.trim().split("=", 2);
                if (kv.length == 2 && "sid".equals(kv[0]))
                    sessions.remove(kv[1]);
            }
        }
        exchange.getResponseHeaders().add("Set-Cookie", "sid=; Path=/; Max-Age=0");
    }

    private static String getRole(String username) {
        if (username == null) return null;
        for (User u : users)
            if (u.getUsername().equals(username)) return u.getRole();
        return null;
    }

    // ── Form helpers ───────────────────────────────────────

    private static Map<String, String> readForm(HttpExchange exchange) throws Exception {
        Map<String, String> map = new HashMap<>();
        InputStream is = exchange.getRequestBody();
        byte[] buf = new byte[1024];
        int len = is.read(buf);
        if (len <= 0) return map;
        String data = new String(buf, 0, len, "UTF-8");
        for (String param : data.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
        }
        return map;
    }

    private static void sendHtml(HttpExchange exchange, String html) throws Exception {
        byte[] bytes = html.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.getResponseHeaders().set("Pragma", "no-cache");
        exchange.getResponseHeaders().set("Expires", "0");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static String getQueryParam(HttpExchange exchange, String name) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(name))
                return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
        }
        return null;
    }

    private static void redirect(HttpExchange exchange, String path) throws Exception {
        exchange.getResponseHeaders().set("Location", path);
        exchange.sendResponseHeaders(303, -1);
    }

    private static Book findBook(String isbn) {
        for (Book b : library)
            if (b.getIsbn().equals(isbn)) return b;
        return null;
    }

    // ── / ──────────────────────────────────────────────────

    private static void handleRoot(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        if (user != null) {
            if ("admin".equals(getRole(user))) { redirect(exchange, "/admin"); return; }
            redirect(exchange, "/dashboard"); return;
        }
        sendHtml(exchange, generateLandingPage());
    }

    private static String generateLandingPage() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>eLibrary — Read. Learn. Grow.</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
<style>
*{margin:0;padding:0;box-sizing:border-box}
html{scroll-behavior:smooth}
body{font-family:'Inter',sans-serif;background:#0a0a1a;color:#e0e0f0;overflow-x:hidden}

/* ── Animated background ── */
.hero{min-height:100vh;display:flex;flex-direction:column;align-items:center;justify-content:center;position:relative;padding:40px 20px;overflow:hidden}
.hero::before{content:'';position:absolute;inset:0;background:radial-gradient(ellipse at 30% 50%,rgba(102,126,234,.15),transparent 60%),radial-gradient(ellipse at 70% 50%,rgba(118,75,162,.12),transparent 60%),radial-gradient(ellipse at 50% 0,rgba(102,126,234,.08),transparent 50%);pointer-events:none}
.particles{position:absolute;inset:0;overflow:hidden;pointer-events:none}
.p{position:absolute;width:4px;height:4px;background:rgba(102,126,234,.4);border-radius:50%;animation:float linear infinite}
.p:nth-child(1){left:10%;top:20%;animation-duration:8s;animation-delay:0s}
.p:nth-child(2){left:25%;top:60%;animation-duration:11s;animation-delay:1s;width:6px;height:6px}
.p:nth-child(3){left:45%;top:10%;animation-duration:9s;animation-delay:2s}
.p:nth-child(4){left:65%;top:70%;animation-duration:12s;animation-delay:.5s;width:3px;height:3px}
.p:nth-child(5){left:80%;top:30%;animation-duration:10s;animation-delay:3s;width:5px;height:5px}
.p:nth-child(6){left:90%;top:80%;animation-duration:7s;animation-delay:1.5s}
.p:nth-child(7){left:50%;top:40%;animation-duration:13s;animation-delay:2.5s;width:3px;height:3px}
.p:nth-child(8){left:15%;top:85%;animation-duration:9s;animation-delay:.8s;width:5px;height:5px}
@keyframes float{0%{transform:translateY(0) scale(1);opacity:.4}50%{transform:translateY(-120px) scale(1.5);opacity:.8}100%{transform:translateY(-240px) scale(1);opacity:0}}

/* ── Nav ── */
nav{position:fixed;top:0;left:0;right:0;z-index:100;padding:16px 40px;display:flex;align-items:center;justify-content:space-between;background:rgba(10,10,26,.7);backdrop-filter:blur(12px);border-bottom:1px solid rgba(255,255,255,.05);animation:slideDown .6s ease}
@keyframes slideDown{from{transform:translateY(-100%);opacity:0}to{transform:translateY(0);opacity:1}}
.logo-text{font-size:1.3em;font-weight:800;background:linear-gradient(135deg,#667eea,#764ba2);-webkit-background-clip:text;-webkit-text-fill-color:transparent;letter-spacing:-.5px}
.nav-links{display:flex;gap:24px;align-items:center}
.nav-links a{color:rgba(255,255,255,.5);text-decoration:none;font-size:.9em;font-weight:500;transition:color .3s}
.nav-links a:hover{color:#fff}
.nav-btn{padding:8px 20px;border-radius:8px;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff!important;font-weight:600!important;transition:transform .3s,box-shadow .3s!important}
.nav-btn:hover{transform:translateY(-2px);box-shadow:0 8px 25px rgba(102,126,234,.35)!important}

/* ── Hero content ── */
.hero-content{text-align:center;position:relative;z-index:2;animation:fadeUp 1s ease .3s both}
@keyframes fadeUp{from{transform:translateY(40px);opacity:0}to{transform:translateY(0);opacity:1}}
.badge{display:inline-block;padding:6px 16px;border-radius:20px;background:rgba(102,126,234,.12);border:1px solid rgba(102,126,234,.2);color:#667eea;font-size:.8em;font-weight:600;margin-bottom:24px;animation:fadeUp 1s ease .5s both;backdrop-filter:blur(4px)}
.hero h1{font-size:clamp(2.5em,7vw,4.5em);font-weight:900;line-height:1.1;margin-bottom:16px;animation:fadeUp 1s ease .6s both}
.hero h1 span{background:linear-gradient(135deg,#667eea,#a855f7,#ec4899);-webkit-background-clip:text;-webkit-text-fill-color:transparent}
.hero p{font-size:clamp(1em,2.5vw,1.2em);color:rgba(255,255,255,.45);max-width:600px;line-height:1.7;margin:0 auto 36px;animation:fadeUp 1s ease .7s both}
.hero-btns{display:flex;gap:16px;justify-content:center;flex-wrap:wrap;animation:fadeUp 1s ease .8s both}
.btn-primary{padding:14px 36px;border:none;border-radius:12px;font-size:1em;font-weight:700;cursor:pointer;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;text-decoration:none;transition:transform .3s,box-shadow .3s}
.btn-primary:hover{transform:translateY(-3px);box-shadow:0 12px 40px rgba(102,126,234,.4)}
.btn-secondary{padding:14px 36px;border:1px solid rgba(255,255,255,.12);border-radius:12px;font-size:1em;font-weight:600;cursor:pointer;background:rgba(255,255,255,.04);color:rgba(255,255,255,.7);text-decoration:none;transition:all .3s}
.btn-secondary:hover{background:rgba(255,255,255,.08);border-color:rgba(255,255,255,.2);transform:translateY(-3px)}

/* ── Stats ── */
.stats{display:flex;gap:48px;margin-top:60px;justify-content:center;flex-wrap:wrap;animation:fadeUp 1s ease 1s both}
.stat{text-align:center}
.stat-num{font-size:2em;font-weight:800;background:linear-gradient(135deg,#667eea,#a855f7);-webkit-background-clip:text;-webkit-text-fill-color:transparent}
.stat-label{color:rgba(255,255,255,.35);font-size:.85em;margin-top:4px}

/* ── Features ── */
.features{padding:100px 20px;position:relative}
.features::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:80%;height:1px;background:linear-gradient(90deg,transparent,rgba(102,126,234,.3),transparent)}
.section-title{text-align:center;font-size:clamp(1.8em,4vw,2.5em);font-weight:800;margin-bottom:12px}
.section-sub{text-align:center;color:rgba(255,255,255,.4);margin-bottom:60px;font-size:1.05em}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:24px;max-width:1100px;margin:0 auto;padding:0 20px}
.card{background:rgba(255,255,255,.03);border:1px solid rgba(255,255,255,.06);border-radius:16px;padding:32px 24px;transition:all .4s;cursor:default;animation:fadeUp .8s ease both}
.card:nth-child(1){animation-delay:.2s}
.card:nth-child(2){animation-delay:.4s}
.card:nth-child(3){animation-delay:.6s}
.card:nth-child(4){animation-delay:.8s}
.card:nth-child(5){animation-delay:1s}
.card:nth-child(6){animation-delay:1.2s}
.card:hover{transform:translateY(-8px);border-color:rgba(102,126,234,.2);box-shadow:0 20px 60px rgba(0,0,0,.3);background:rgba(255,255,255,.06)}
.card-icon{font-size:2em;margin-bottom:16px;display:block}
.card h3{font-size:1.15em;font-weight:700;margin-bottom:8px}
.card p{color:rgba(255,255,255,.4);font-size:.9em;line-height:1.6}

/* ── CTA ── */
.cta{padding:80px 20px 100px;text-align:center;position:relative}
.cta::before{content:'';position:absolute;bottom:0;left:50%;transform:translateX(-50%);width:80%;height:1px;background:linear-gradient(90deg,transparent,rgba(102,126,234,.3),transparent)}
.cta h2{font-size:clamp(1.6em,4vw,2.2em);font-weight:800;margin-bottom:16px}
.cta p{color:rgba(255,255,255,.4);margin-bottom:32px}
.cta .btn-primary{display:inline-block}

/* ── Footer ── */
footer{padding:30px 20px;text-align:center;color:rgba(255,255,255,.2);font-size:.85em}
footer a{color:rgba(255,255,255,.3);text-decoration:none;transition:color .3s}
footer a:hover{color:#667eea}

/* ── Responsive ── */
@media(max-width:600px){nav{padding:14px 20px}.hero h1{font-size:2em}.stats{gap:24px}}
</style>
</head>
<body>

<nav>
  <span class="logo-text">&#128218; eLibrary</span>
  <div class="nav-links">
    <a href="#features">Features</a>
    <a href="/login" class="nav-btn">Get Started</a>
  </div>
</nav>

<section class="hero">
  <div class="particles">
    <div class="p"></div><div class="p"></div><div class="p"></div>
    <div class="p"></div><div class="p"></div><div class="p"></div>
    <div class="p"></div><div class="p"></div>
  </div>
  <div class="hero-content">
    <div class="badge">&#9733; Tanzania Institute of Education</div>
    <h1>Your Digital<br><span>Library</span> Awaits</h1>
    <p>Access hundreds of TIE textbooks online — anytime, anywhere. Read Pre-Primary to Form 6 books for free.</p>
    <div class="hero-btns">
      <a href="/login" class="btn-primary">Get Started &#8594;</a>
      <a href="#features" class="btn-secondary">Learn More</a>
    </div>
    <div class="stats">
       <div class="stat"><div class="stat-num">280+</div><div class="stat-label">Books</div></div>
      <div class="stat"><div class="stat-num">PrePrimary - Form 6</div><div class="stat-label">Classes</div></div>
      <div class="stat"><div class="stat-num">&#8734;</div><div class="stat-label">Free Access</div></div>
    </div>
  </div>
</section>

<section class="features" id="features">
  <h2 class="section-title">Why eLibrary?</h2>
  <p class="section-sub">Everything you need in one place</p>
  <div class="grid">
    <div class="card"><span class="card-icon">&#128214;</span><h3>All Subjects</h3><p>Mathematics, Science, Languages, Vocational — every TIE subject covered.</p></div>
    <div class="card"><span class="card-icon">&#128640;</span><h3>Instant Access</h3><p>No downloads. Open any book instantly in your browser and start reading.</p></div>
    <div class="card"><span class="card-icon">&#128269;</span><h3>Smart Search</h3><p>Filter by subject, form, or ISBN. Find the exact book you need in seconds.</p></div>
    <div class="card"><span class="card-icon">&#128187;</span><h3>Any Device</h3><p>Works on phone, tablet, or laptop. Read anywhere, anytime.</p></div>
    <div class="card"><span class="card-icon">&#128221;</span><h3>Flipbook Reader</h3><p>Real page-flipping experience embedded right in your browser.</p></div>
    <div class="card"><span class="card-icon">&#128109;</span><h3>Track Progress</h3><p>Save your reading history and pick up where you left off.</p></div>
  </div>
</section>

<section class="cta">
  <h2>Ready to Start Reading?</h2>
  <p>Join thousands of students using eLibrary today.</p>
  <a href="/login" class="btn-primary">Get Started &#8594;</a>
</section>

<footer>
  &copy; 2026 <a href="https://www.tie.go.tz" target="_blank">Tanzania Institute of Education</a> &mdash; eLibrary System
</footer>

</body>
</html>
""";
    }

    // ── Signup ─────────────────────────────────────────────

    private static void handleSignup(HttpExchange exchange) throws Exception {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String password = form.getOrDefault("password", "").trim();
            String phone = form.getOrDefault("phone", "").trim();
            String secQ = form.getOrDefault("securityQuestion", "").trim();
            String secA = form.getOrDefault("securityAnswer", "").trim();
            if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || secQ.isEmpty() || secA.isEmpty()) {
                sendHtml(exchange, generateSignupPage("<span class='warn-icon'>&#9888;&#65039;</span><span><strong>Incomplete Form</strong><br>Please fill in all fields to create your account.</span>"));
                return;
            }
            for (User u : users)
                if (u.getUsername().equals(username)) {
                    sendHtml(exchange, generateSignupPage("Username already taken."));
                    return;
                }
            users.add(new User(username, password, "user", phone, secQ, secA));
            saveUsers();
            redirect(exchange, "/login?signup=1");
            return;
        }
        sendHtml(exchange, generateSignupPage(null));
    }

    private static String generateSignupPage(String error) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Sign Up &mdash; eLibrary System</title>
            <style>
            *{box-sizing:border-box;margin:0;padding:0}
            body{font-family:'Inter','Segoe UI',sans-serif;background:#0b0b1a;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px;position:relative;overflow:hidden}
            body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=1600') center/cover no-repeat;filter:brightness(.12) blur(3px);pointer-events:none}
            .box{background:rgba(18,18,40,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 40px;box-shadow:0 25px 80px rgba(0,0,0,.6),inset 0 1px 0 rgba(255,255,255,.05);width:100%;max-width:400px;text-align:center;position:relative;z-index:1}
            .box::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:60%;height:2px;background:linear-gradient(90deg,transparent,#667eea,#764ba2,transparent)}
            .logo{font-size:44px;margin-bottom:12px;display:block;filter:drop-shadow(0 4px 12px rgba(102,126,234,.3))}
            h2{color:#e8e8ff;margin-bottom:4px;font-size:1.5em;font-weight:700;letter-spacing:-.5px}
            .sub{color:rgba(255,255,255,.4);font-size:.9em;margin-bottom:28px}
            .input-group{position:relative;margin-bottom:14px}
            .input-group .icon{position:absolute;left:16px;top:50%;transform:translateY(-50%);color:rgba(255,255,255,.25);font-size:16px;pointer-events:none}
            .input-group input,.input-group select{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none;-webkit-appearance:none;appearance:none;cursor:pointer}
            .input-group input::placeholder{color:rgba(255,255,255,.2)}
            .input-group input:focus,.input-group select:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.06);box-shadow:0 0 0 3px rgba(102,126,234,.1)}
            .input-group select{background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='9' fill='%238888cc'%3E%3Cpath d='M1 1l6 6 6-6'/%3E%3C/svg%3E");background-repeat:no-repeat;background-position:right 16px center;padding-right:44px}
            .input-group select option{background:#1a1a3a;color:#e0e0f0}
            .btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}
            .btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}
            .btn:active{transform:translateY(0)}
            .err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:14px 18px;border-radius:12px;margin-bottom:20px;font-size:.88em;text-align:left;display:flex;align-items:center;gap:12px;animation:shake .4s ease-out}
            .err .warn-icon{font-size:1.6em;flex-shrink:0}
            .err strong{display:block;font-size:.95em;margin-bottom:2px}
            @keyframes shake{0%,100%{transform:translateX(0)}20%{transform:translateX(-6px)}40%{transform:translateX(6px)}60%{transform:translateX(-4px)}80%{transform:translateX(4px)}}
            .footer{margin-top:24px;display:flex;flex-direction:column;gap:10px}
            .footer a{color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}
            .footer a:hover{color:#667eea}
            .input-group{position:relative}
            .toggle-pwd{position:absolute;right:16px;top:50%;transform:translateY(-50%);cursor:pointer;color:rgba(255,255,255,.35);font-size:18px;user-select:none;z-index:2}
            .toggle-pwd:hover{color:rgba(255,255,255,.6)}
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style>
            <script>function togglePwd(id,el){var inp=document.getElementById(id);if(inp.type==='password'){inp.type='text';el.innerHTML='&#128064;'}else{inp.type='password';el.innerHTML='&#128065;'}}</script>
            </head><body>
            <div class="box">
            <span class="logo">&#128218;</span>
            <h2>Create Account</h2>
            <p class="sub">Join the eLibrary today</p>
            """ + (error != null ? "<div class='err'>" + error + "</div>" : "") + """
            <form method='POST'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' id='spwd' placeholder='Password' required><span class='toggle-pwd' onclick="togglePwd('spwd',this)">&#128065;</span></div>
            <div class="input-group"><span class="icon">&#128222;</span><input type='tel' name='phone' id='sphone' placeholder='e.g. 0762123456' pattern='0[0-9]{9}' maxlength='10' title='Enter a 10-digit phone number starting with 0' required><script>document.getElementById('sphone').addEventListener('input',function(){this.value=this.value.replace(/[^0-9]/g,'')})</script></div>
            <div class="input-group"><span class="icon">&#128220;</span><select name='securityQuestion' required><option value=''>Select a security question...</option><option value='What is your mothers maiden name?'>What is your mother's maiden name?</option><option value='What was the name of your first pet?'>What was the name of your first pet?</option><option value='What city were you born in?'>What city were you born in?</option><option value='What is your favorite book?'>What is your favorite book?</option><option value='What is the name of your primary school?'>What is the name of your primary school?</option></select></div>
            <div class="input-group"><span class="icon">&#128221;</span><input type='text' name='securityAnswer' placeholder='Security answer' required></div>
            <button type='submit' class='btn'>Sign Up</button>
            </form>
            <div class="footer">
            <a href='/login'>Already have an account? Login</a>
            <a href='/login'>&larr; Back</a>
            </div>
            </div></body></html>
            """;
    }

    // ── Login ──────────────────────────────────────────────

    private static void handleLogin(HttpExchange exchange) throws Exception {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String password = form.getOrDefault("password", "").trim();
            for (User u : users) {
                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                    setSession(exchange, username);
                    if ("admin".equals(u.getRole())) redirect(exchange, "/admin");
                    else redirect(exchange, "/dashboard");
                    return;
                }
            }
            sendHtml(exchange, generateLoginPage("Invalid credentials.", false, false));
            return;
        }
        boolean justRegistered = "1".equals(getQueryParam(exchange, "signup"));
        boolean justReset = "1".equals(getQueryParam(exchange, "reset"));
        sendHtml(exchange, generateLoginPage(null, justRegistered, justReset));
    }

    private static String generateLoginPage(String error, boolean registered, boolean justReset) {
        StringBuilder h = new StringBuilder();
        h.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Login &mdash; eLibrary System</title>
            <style>
            *{box-sizing:border-box;margin:0;padding:0}
            body{font-family:'Inter','Segoe UI',sans-serif;background:#0b0b1a;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px;position:relative;overflow:hidden}
            body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=1600') center/cover no-repeat;filter:brightness(.15) blur(3px);pointer-events:none}
            .box{background:rgba(18,18,40,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 40px;box-shadow:0 25px 80px rgba(0,0,0,.6),inset 0 1px 0 rgba(255,255,255,.05);width:100%;max-width:400px;text-align:center;position:relative}
            .box::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:60%;height:2px;background:linear-gradient(90deg,transparent,#667eea,#764ba2,transparent)}
            .logo{font-size:44px;margin-bottom:12px;display:block;filter:drop-shadow(0 4px 12px rgba(102,126,234,.3))}
            h2{color:#e8e8ff;margin-bottom:4px;font-size:1.5em;font-weight:700;letter-spacing:-.5px}
            .sub{color:rgba(255,255,255,.4);font-size:.9em;margin-bottom:28px}
            .input-group{position:relative;margin-bottom:14px}
            .input-group .icon{position:absolute;left:16px;top:50%;transform:translateY(-50%);color:rgba(255,255,255,.25);font-size:16px;pointer-events:none}
            .input-group input,.input-group select{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none;-webkit-appearance:none;appearance:none;cursor:pointer}
            .input-group input::placeholder{color:rgba(255,255,255,.2)}
            .input-group input:focus,.input-group select:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.06);box-shadow:0 0 0 3px rgba(102,126,234,.1)}
            .input-group select{background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='9' fill='%238888cc'%3E%3Cpath d='M1 1l6 6 6-6'/%3E%3C/svg%3E");background-repeat:no-repeat;background-position:right 16px center;padding-right:44px}
            .input-group select option{background:#1a1a3a;color:#e0e0f0}
            .btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}
            .btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}
            .btn:active{transform:translateY(0)}
            .err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:12px 16px;border-radius:10px;margin-bottom:18px;font-size:.85em;text-align:left}
            .suc{color:#2ecc71;background:rgba(46,204,113,.1);border:1px solid rgba(46,204,113,.2);padding:14px 18px;border-radius:12px;margin-bottom:20px;font-size:.88em;text-align:left;display:flex;align-items:center;gap:12px;animation:fadeIn .5s ease-out}
            .suc .suc-icon{font-size:1.4em;flex-shrink:0}
            .suc strong{display:block;font-size:.95em;margin-bottom:2px}
            @keyframes fadeIn{0%{opacity:0;transform:translateY(-6px)}100%{opacity:1;transform:translateY(0)}}
            .footer{margin-top:24px;display:flex;flex-direction:column;gap:10px}
            .footer a{color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}
            .footer a:hover{color:#667eea}
            .input-group{position:relative}
            .toggle-pwd{position:absolute;right:16px;top:50%;transform:translateY(-50%);cursor:pointer;color:rgba(255,255,255,.35);font-size:18px;user-select:none;z-index:2}
            .toggle-pwd:hover{color:rgba(255,255,255,.6)}
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style>
            <script>function togglePwd(id,el){var inp=document.getElementById(id);if(inp.type==='password'){inp.type='text';el.innerHTML='&#128064;'}else{inp.type='password';el.innerHTML='&#128065;'}}</script>
            </head><body>
            <div class="box">
            <span class="logo">&#128218;</span>
            <h2>Welcome to eLibrary</h2>
            <p class="sub">Sign in to your account</p>
            """);
        if (error != null) h.append("<div class='err'>").append(error).append("</div>");
        if (registered) h.append("<div class='suc'><span class='suc-icon'>&#9989;</span><span><strong>Account Created!</strong><br>Your account was created successfully. Please sign in.</span></div>");
        if (justReset) h.append("<div class='suc'><span class='suc-icon'>&#9989;</span><span><strong>Password Reset!</strong><br>Your password has been reset successfully. Please sign in.</span></div>");
        h.append("""
            <form method='POST'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' id='lpwd' placeholder='Password' required><span class='toggle-pwd' onclick="togglePwd('lpwd',this)">&#128065;</span></div>
            <button type='submit' class='btn'>Login</button>
            </form>
            <div class="footer">
            <a href='/signup'>No account? Sign up</a>
            <a href='/forgot-password' style='font-size:.82em;color:rgba(255,255,255,.25)'>Forgot Password?</a>
            </div>
            </div></body></html>
            """);
        return h.toString();
    }

    // ── Logout ─────────────────────────────────────────────

    private static void handleLogout(HttpExchange exchange) throws Exception {
        clearSession(exchange);
        redirect(exchange, "/login");
    }

    // ── Forgot Password ────────────────────────────────────

    private static void handleForgotPassword(HttpExchange exchange) throws Exception {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String answer = form.getOrDefault("answer", "").trim();
            String newPass = form.getOrDefault("newPassword", "").trim();

            // Step 2: verify security answer and set new password
            if (!answer.isEmpty() && !newPass.isEmpty()) {
                User target = null;
                for (User u : users) {
                    if (u.getUsername().equals(username)) {
                        target = u;
                        break;
                    }
                }
                if (target == null || target.getSecurityAnswer().isEmpty()) {
                    sendHtml(exchange, generateForgotPasswordPage(null, "User not found or no security question set.", false));
                    return;
                }
                if (!target.getSecurityAnswer().equalsIgnoreCase(answer)) {
                    sendHtml(exchange, generateForgotPasswordPage(username, "Incorrect answer. Please try again.", false));
                    return;
                }
                users.remove(target);
                users.add(new User(username, newPass, target.getRole(), target.getPhoneNumber(), target.getSecurityQuestion(), target.getSecurityAnswer()));
                saveUsers();
                redirect(exchange, "/login?reset=1");
                return;
            }

            // Step 1: show security question
            if (!username.isEmpty()) {
                User target = null;
                for (User u : users) {
                    if (u.getUsername().equals(username)) {
                        target = u;
                        break;
                    }
                }
                if (target == null || target.getSecurityQuestion().isEmpty()) {
                    sendHtml(exchange, generateForgotPasswordPage(null, "User not found or no security question set.", false));
                    return;
                }
                sendHtml(exchange, generateForgotPasswordPage(username, target.getSecurityQuestion(), true));
                return;
            }

            sendHtml(exchange, generateForgotPasswordPage(null, null, false));
            return;
        }
        sendHtml(exchange, generateForgotPasswordPage(null, null, false));
    }

    private static String generateForgotPasswordPage(String username, String message, boolean isQuestion) {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>Forgot Password &mdash; eLibrary System</title>");
        h.append("<style>");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Inter','Segoe UI',sans-serif;background:#0b0b1a;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px;position:relative;overflow:hidden}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=1600') center/cover no-repeat;filter:brightness(.15) blur(3px);pointer-events:none}");
        h.append(".box{background:rgba(18,18,40,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 40px;box-shadow:0 25px 80px rgba(0,0,0,.6),inset 0 1px 0 rgba(255,255,255,.05);width:100%;max-width:420px;text-align:center;position:relative}");
        h.append(".box::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:60%;height:2px;background:linear-gradient(90deg,transparent,#667eea,#764ba2,transparent)}");
        h.append(".logo{font-size:44px;margin-bottom:12px;display:block;filter:drop-shadow(0 4px 12px rgba(102,126,234,.3))}");
        h.append("h2{color:#e8e8ff;margin-bottom:4px;font-size:1.5em;font-weight:700;letter-spacing:-.5px}");
        h.append(".sub{color:rgba(255,255,255,.4);font-size:.9em;margin-bottom:28px}");
        h.append(".input-group{position:relative;margin-bottom:14px}");
        h.append(".input-group .icon{position:absolute;left:16px;top:50%;transform:translateY(-50%);color:rgba(255,255,255,.25);font-size:16px;pointer-events:none}");
        h.append(".input-group input,.input-group select{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none;-webkit-appearance:none;appearance:none;cursor:pointer}");
        h.append(".input-group input::placeholder{color:rgba(255,255,255,.2)}");
        h.append(".input-group input:focus,.input-group select:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.06);box-shadow:0 0 0 3px rgba(102,126,234,.1)}");
        h.append(".input-group select{background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='9' fill='%238888cc'%3E%3Cpath d='M1 1l6 6 6-6'/%3E%3C/svg%3E\");background-repeat:no-repeat;background-position:right 16px center;padding-right:44px}");
        h.append(".input-group select option{background:#1a1a3a;color:#e0e0f0}");
        h.append(".btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}");
        h.append(".btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}");
        h.append(".btn:active{transform:translateY(0)}");
        h.append(".msg{color:#f1c40f;background:rgba(241,196,15,.1);border:1px solid rgba(241,196,15,.2);padding:14px 18px;border-radius:12px;margin-bottom:20px;font-size:.88em;text-align:left;line-height:1.5}");
        h.append(".err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:12px 16px;border-radius:10px;margin-bottom:18px;font-size:.85em;text-align:left}");
        h.append(".link{display:block;margin-top:18px;color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}");
        h.append(".link:hover{color:#667eea}");
        h.append("@media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}");
        h.append("</style></head><body>");

        if (username != null && isQuestion) {
            // Show security question
            h.append("<div class='box'><span class='logo'>&#128273;</span>");
            h.append("<h2>Reset Password</h2>");
            h.append("<p class='sub'>Answer your security question</p>");
            h.append("<div class='msg'><strong>Security Question:</strong><br>").append(message).append("</div>");
            h.append("<form method='POST'>");
            h.append("<input type='hidden' name='username' value='").append(username).append("'>");
            h.append("<div class='input-group'><span class='icon'>&#128221;</span><input type='text' name='answer' placeholder='Your answer' required></div>");
            h.append("<div class='input-group'><span class='icon'>&#128273;</span><input type='password' name='newPassword' placeholder='New password' required></div>");
            h.append("<button type='submit' class='btn'>Reset Password</button>");
            h.append("</form>");
            h.append("<a href='/login' class='link'>&larr; Back to Login</a>");
            h.append("</div>");
        } else {
            // Show username form
            h.append("<div class='box'><span class='logo'>&#128273;</span>");
            h.append("<h2>Forgot Password</h2>");
            h.append("<p class='sub'>Enter your username to reset your password</p>");
            if (message != null) h.append("<div class='err'>").append(message).append("</div>");
            h.append("<form method='POST'>");
            h.append("<div class='input-group'><span class='icon'>&#128100;</span><input type='text' name='username' placeholder='Username' required></div>");
            h.append("<button type='submit' class='btn'>Continue</button>");
            h.append("</form>");
            h.append("<a href='/login' class='link'>&larr; Back to Login</a>");
            h.append("</div>");
        }

        h.append("</body></html>");
        return h.toString();
    }

    // ── Auto-return expired borrows ───────────────────────
    private static void cleanExpiredBorrows() {
        List<BorrowRecord> expired = borrows.stream().filter(BorrowRecord::isOverdue).collect(Collectors.toList());
        if (!expired.isEmpty()) {
            borrows.removeAll(expired);
            saveBorrows();
        }
    }

    // ── Dashboard (users) ──────────────────────────────────

    private static void handleDashboard(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        if (user == null) { redirect(exchange, "/login"); return; }
        if ("admin".equals(getRole(user))) { redirect(exchange, "/admin"); return; }

        cleanExpiredBorrows();

        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String action = form.getOrDefault("action", "");
            String isbn = form.getOrDefault("isbn", "");

            if ("return".equals(action)) {
                borrows.removeIf(br -> br.isbn.equals(isbn) && br.username.equals(user));
                saveBorrows();
            } else if ("cancel".equals(action)) {
                requests.removeIf(r -> r.getIsbn().equals(isbn) && r.getUsername().equals(user) && "pending".equals(r.getStatus()));
                saveRequests();
            } else {
                if (findBook(isbn) != null) {
                    requests.add(new Request(isbn, user));
                    saveRequests();
                }
            }
            redirect(exchange, "/dashboard");
            return;
        }
        sendHtml(exchange, generateDashboard(user));
    }

    private static String escapeJS(String s) {
        return s.replace("'", "\\'").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }

    private static String generateDashboard(String username) {
        long avail = library.size();

        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>Dashboard &mdash; eLibrary System</title><style>");
        h.append("@keyframes fadeUp{0%{opacity:0;transform:translateY(30px)}100%{opacity:1;transform:translateY(0)}}");
        h.append("@keyframes fadeIn{0%{opacity:0}100%{opacity:1}}");
        h.append("@keyframes pulse{0%{transform:scale(1)}50%{transform:scale(1.05)}100%{transform:scale(1)}}");
        h.append("@keyframes shimmer{0%{background-position:-200% 0}100%{background-position:200% 0}}");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Inter','Segoe UI',sans-serif;min-height:100vh;padding:20px;position:relative;overflow-x:hidden}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=1600') center/cover no-repeat;filter:brightness(.08) blur(3px);pointer-events:none}");
        h.append(".container{max-width:1100px;margin:0 auto;position:relative;z-index:1;animation:fadeUp .6s ease-out}");
        h.append(".topbar{display:flex;justify-content:space-between;align-items:center;padding:16px 28px;background:rgba(16,16,36,.8);border:1px solid rgba(255,255,255,.06);border-radius:16px;margin-bottom:28px;backdrop-filter:blur(14px);animation:fadeIn .8s ease-out}");
        h.append(".topbar .brand{color:#e0e0f0;font-size:1.05em;font-weight:600;letter-spacing:.3px;display:flex;align-items:center;gap:8px}");
        h.append(".topbar .brand span{color:#667eea}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 20px;border:1px solid rgba(255,255,255,.08);border-radius:10px;font-size:.88em;transition:all .3s;position:relative;overflow:hidden}");
        h.append(".topbar a::before{content:'';position:absolute;inset:0;background:linear-gradient(135deg,#667eea,#764ba2);opacity:0;transition:opacity .3s;border-radius:10px}");
        h.append(".topbar a:hover::before{opacity:.15}");
        h.append(".topbar a:hover{color:#fff;border-color:#667eea;transform:translateY(-2px);box-shadow:0 6px 20px rgba(102,126,234,.2)}");
        h.append("h1{color:#e8e8ff;text-align:center;margin-bottom:4px;font-size:1.9em;font-weight:700;letter-spacing:-.5px;animation:fadeIn .6s ease-out .1s both}.sub{color:rgba(255,255,255,.35);text-align:center;margin-bottom:28px;font-size:.95em;animation:fadeIn .6s ease-out .15s both}");
        h.append(".stats{display:flex;gap:14px;margin-bottom:28px;flex-wrap:wrap}");
        h.append(".stat{padding:20px 24px;flex:1;min-width:130px;text-align:center;border-radius:14px;border:1px solid rgba(255,255,255,.06);transition:all .4s cubic-bezier(.25,.46,.45,.94);position:relative;overflow:hidden}");
        h.append(".stat::after{content:'';position:absolute;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(circle,var(--glow,transparent) 0%,transparent 70%);opacity:0;transition:opacity .4s}");
        h.append(".stat:hover::after{opacity:1}");
        h.append(".stat .n{font-size:2em;font-weight:700;display:block;position:relative;z-index:1}.stat .l{font-size:.82em;margin-top:5px;opacity:.85;text-transform:uppercase;letter-spacing:.5px;position:relative;z-index:1}");
        h.append(".stat.avail{background:rgba(39,174,96,.1);color:#2ecc71;border-color:rgba(39,174,96,.15);--glow:rgba(39,174,96,.15)}");
        h.append(".stat.avail:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(39,174,96,.2);border-color:rgba(39,174,96,.3)}");
        h.append(".stat.req{background:rgba(243,156,18,.08);color:#f1c40f;border-color:rgba(243,156,18,.12);--glow:rgba(243,156,18,.12)}");
        h.append(".stat.req:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(243,156,18,.2);border-color:rgba(243,156,18,.3)}");
        h.append(".stat.bor{background:rgba(231,76,60,.08);color:#e74c3c;border-color:rgba(231,76,60,.12);--glow:rgba(231,76,60,.12)}");
        h.append(".stat.bor:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(231,76,60,.2);border-color:rgba(231,76,60,.3)}");
        h.append(".stat.tot{background:rgba(39,174,96,.08);color:#2ecc71;border-color:rgba(39,174,96,.12);--glow:rgba(39,174,96,.1)}");
        h.append(".stat.tot:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(39,174,96,.2);border-color:rgba(39,174,96,.3)}");
        h.append(".card{background:rgba(16,16,36,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:18px;padding:28px;margin-bottom:24px;box-shadow:0 8px 50px rgba(0,0,0,.3);transition:all .4s cubic-bezier(.25,.46,.45,.94);animation:fadeUp .6s ease-out .2s both}");
        h.append(".card:hover{border-color:rgba(255,255,255,.1);box-shadow:0 12px 60px rgba(0,0,0,.4);transform:translateY(-2px)}");
        h.append(".card h2{font-size:1.1em;color:#d0d0f0;margin-bottom:16px;display:flex;align-items:center;gap:10px;font-weight:600;letter-spacing:.2px}");
        h.append("table{width:100%;border-collapse:collapse}");
        h.append("th{padding:14px 12px;text-align:left;font-size:.75em;text-transform:uppercase;letter-spacing:.7px;color:rgba(255,255,255,.3);font-weight:700;border-bottom:1px solid rgba(255,255,255,.06)}");
        h.append("td{padding:14px 12px;border-bottom:1px solid rgba(255,255,255,.04);font-size:.9em;color:rgba(255,255,255,.65);transition:all .3s}");
        h.append("tr{transition:background .2s}");
        h.append("tr:nth-child(even) td{background:rgba(255,255,255,.015)}");
        h.append("tr:hover td{color:rgba(255,255,255,.9);background:rgba(102,126,234,.06)}");
        h.append("tr:last-child td{border-bottom:none}");
        h.append(".sa{color:#2ecc71;font-weight:600;text-shadow:0 0 20px rgba(46,204,113,.15)}.sr{color:#f1c40f;font-weight:600}.sb{color:#e74c3c;font-weight:600}.sy{color:#f1c40f;font-weight:600;text-shadow:0 0 20px rgba(241,196,15,.15)}");
        h.append("select,input{padding:13px 18px;border:1px solid rgba(255,255,255,.06);border-radius:12px;font-size:14px;flex:1;min-width:0;transition:all .3s;background:rgba(255,255,255,.03);color:#e0e0f0;outline:none;appearance:none;-webkit-appearance:none;cursor:pointer}");
        h.append("select{background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='9' fill='%23666ea'%3E%3Cpath d='M1 1l6 6 6-6'/%3E%3C/svg%3E\");background-repeat:no-repeat;background-position:right 16px center;padding-right:44px}");
        h.append("select option{background:#151530;color:#e0e0f0;padding:12px}");
        h.append("select:hover,input:hover{border-color:rgba(102,126,234,.25);background:rgba(255,255,255,.05);transform:translateY(-1px)}");
        h.append("select:focus,input:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.07);box-shadow:0 0 0 4px rgba(102,126,234,.08);transform:translateY(-1px)}");
        h.append(".btn{padding:13px 30px;border:none;border-radius:12px;cursor:pointer;font-size:13px;font-weight:600;color:#fff;transition:all .3s cubic-bezier(.25,.46,.45,.94);white-space:nowrap;letter-spacing:.3px;position:relative;overflow:hidden}");
        h.append(".btn::after{content:'';position:absolute;inset:0;background:linear-gradient(135deg,transparent 30%,rgba(255,255,255,.1) 50%,transparent 70%);background-size:200% 100%;transition:background .5s}");
        h.append(".btn:hover::after{background-position:100% 0}");
        h.append(".btn-sm{padding:7px 16px;font-size:11px;border-radius:8px;letter-spacing:.2px}");
        h.append(".btn:hover{transform:translateY(-3px)}");
        h.append(".btn:active{transform:translateY(-1px)}");
        h.append(".btn-pri{background:linear-gradient(135deg,#667eea,#764ba2);box-shadow:0 4px 20px rgba(102,126,234,.2)}");
        h.append(".btn-pri:hover{box-shadow:0 8px 30px rgba(102,126,234,.35)}");
        h.append(".btn-suc{background:linear-gradient(135deg,#27ae60,#2ecc71);box-shadow:0 4px 20px rgba(39,174,96,.2)}");
        h.append(".btn-suc:hover{box-shadow:0 8px 30px rgba(39,174,96,.35)}");
        h.append(".btn-rj{background:linear-gradient(135deg,#e74c3c,#ff6b6b);box-shadow:0 4px 20px rgba(231,76,60,.15)}");
        h.append(".btn-rj:hover{box-shadow:0 8px 25px rgba(231,76,60,.3)}");
        h.append(".topbar .btn-nav{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border:none;padding:8px 22px;text-decoration:none;font-size:.88em;font-weight:600;border-radius:10px;transition:all .3s cubic-bezier(.25,.46,.45,.94);display:inline-block}");
        h.append(".topbar .btn-nav:hover{transform:translateY(-2px);box-shadow:0 8px 25px rgba(102,126,234,.35);color:#fff}");
        h.append(".empty{text-align:center;padding:35px;color:rgba(255,255,255,.2);font-size:.9em}");
        h.append(".filter-row{display:flex;gap:14px;flex-wrap:wrap;margin-bottom:18px}");
        h.append(".filter-row select{flex:1;min-width:140px}");
        h.append("@media(max-width:500px){.filter-row{gap:8px}.filter-row select{flex:1 1 100%;min-width:0;font-size:13px;padding:11px 14px}}");
        h.append(".table-wrap{overflow-x:auto;-webkit-overflow-scrolling:touch;margin:0 -4px}");
        h.append(".table-wrap table{min-width:480px}");
        h.append("@media(max-width:500px){.table-wrap table{min-width:0;width:100%}.table-wrap table,.table-wrap tbody,.table-wrap tr{display:block}.table-wrap th{display:none}.table-wrap td{display:grid;grid-template-columns:80px 1fr;gap:4px 10px;padding:10px 12px;align-items:center}.table-wrap td:before{content:attr(data-label);font-weight:600;color:#8888bb;font-size:.78em;text-transform:uppercase;letter-spacing:.5px}.table-wrap tr+tr{border-top:1px solid rgba(255,255,255,.05)}}");
        h.append(".form-row{display:flex;gap:12px;flex-wrap:wrap;align-items:center}");
        h.append(".form-label{color:#c8c8e8;font-size:.9em;font-weight:600;margin-bottom:8px;display:block}");
        h.append(".form-group{flex:1;min-width:210px}");
        h.append("@media(max-width:600px){body{padding:12px}.stats{gap:10px}.stat{padding:15px}.stat .n{font-size:1.4em}}");
        h.append(".wa-fab{position:fixed;bottom:28px;right:28px;width:64px;height:64px;border-radius:50%;background:linear-gradient(135deg,#25D366,#128C7e);display:flex;align-items:center;justify-content:center;color:#fff;text-decoration:none;font-size:30px;box-shadow:0 6px 30px rgba(37,211,102,.35);transition:all .4s cubic-bezier(.25,.46,.45,.94);z-index:999;animation:fadeUp .8s ease-out .5s both}");
        h.append(".wa-fab::before{content:'';position:absolute;inset:-6px;border-radius:50%;background:linear-gradient(135deg,#25D366,#128C7e);opacity:.25;filter:blur(12px);z-index:-1;animation:pulse 2.5s ease-in-out infinite}");
        h.append(".wa-fab:hover{transform:scale(1.12) translateY(-4px);box-shadow:0 12px 50px rgba(37,211,102,.5)}");
        h.append(".wa-tooltip{position:fixed;bottom:100px;right:28px;background:rgba(16,16,36,.92);backdrop-filter:blur(12px);border:1px solid rgba(255,255,255,.08);border-radius:14px;padding:12px 20px;color:rgba(255,255,255,.8);font-size:.82em;box-shadow:0 8px 30px rgba(0,0,0,.4);animation:fadeUp .6s ease-out .7s both;z-index:998;pointer-events:none}");
        h.append(".wa-tooltip::after{content:'';position:absolute;bottom:-8px;right:24px;width:14px;height:14px;background:rgba(16,16,36,.92);border-right:1px solid rgba(255,255,255,.08);border-bottom:1px solid rgba(255,255,255,.08);transform:rotate(45deg);border-radius:2px}");
        h.append("</style>");
        // ── JS filter data ──────────────────────────────
        h.append("<script>");
        h.append("var books=[");
        boolean first=true;
        for (Book b : library) {
            if (!first) h.append(",");
            first=false;
            h.append("{i:'").append(b.getIsbn()).append("',t:'").append(escapeJS(b.getTitle())).append("',a:'").append(escapeJS(b.getAuthor())).append("',tp:'").append(b.getType()).append("',l:'").append(b.getLevel()).append("',c:'").append(b.getClassName()).append("'}");
        }
        h.append("];");
        h.append("function resetFilters(keepType){if(!keepType){document.getElementById('ftype').value=''}document.getElementById('flevel').innerHTML='<option value=\"\">Select level first...</option>';document.getElementById('flevel').disabled=true;document.getElementById('fclass').innerHTML='<option value=\"\">Select class first...</option>';document.getElementById('fclass').disabled=true;filterBooks()}");
        h.append("var levelOrder=['Pre-Primary','Primary','Secondary','A-Level','Novel','All'];");
h.append("function populateLevel(){var t=document.getElementById('ftype').value;if(!t){resetFilters(true);return}var lv=document.getElementById('flevel');lv.innerHTML='<option value=\"\">Select level...</option>';lv.disabled=false;var seen={};books.forEach(function(b){if(b.tp===t)seen[b.l]=true});levelOrder.forEach(function(l){if(seen[l])lv.innerHTML+='<option value=\"'+l+'\">'+l+'</option>'});lv.value='';document.getElementById('fclass').innerHTML='<option value=\"\">Select class first...</option>';document.getElementById('fclass').disabled=true;filterBooks()}");
        h.append("var classOrder={ 'Nursery':0,'Standard 1':1,'Standard 2':2,'Standard 3':3,'Standard 4':4,'Standard 5':5,'Standard 6':6,'Standard 7':7,'Play':8,'Novel':9,'Form 1':10,'Form 2':11,'Form 3':12,'Form 4':13,'Form 5':14,'Form 6':15 };");
h.append("function populateClass(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;if(!lv){document.getElementById('fclass').innerHTML='<option value=\"\">Select class first...</option>';document.getElementById('fclass').disabled=true;filterBooks();return}var cls=document.getElementById('fclass');cls.innerHTML='<option value=\"\">Select class...</option>';cls.disabled=false;var seen={};books.forEach(function(b){if(b.tp===t&&b.l===lv&&!seen[b.c]){seen[b.c]=true}});var sorted=Object.keys(seen).sort(function(a,b){return(classOrder[a]||99)-(classOrder[b]||99)});sorted.forEach(function(c){cls.innerHTML+='<option value=\"'+c+'\">'+c+'</option>'});filterBooks()}");
        h.append("function filterBooks(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;var cl=document.getElementById('fclass').value;if(!t&&!lv&&!cl){document.getElementById('btable').innerHTML='<tr><td colspan=3 class=empty>Select a filter above to browse books</td></tr>';return}var html='';books.forEach(function(b){if((!t||b.tp===t)&&(!lv||b.l===lv)&&(!cl||b.c===cl)){html+='<tr><td data-label=\"ISBN\">'+b.i+'</td><td data-label=\"Title\">'+b.t+'</td><td data-label=\"Author\">'+b.a+'</td></tr>'}});document.getElementById('btable').innerHTML=html||'<tr><td colspan=3 class=empty>No books found</td></tr>';populateRequest()}");
h.append("function populateRequest(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;var cl=document.getElementById('fclass').value;var sel=document.getElementById('reqisbn');sel.innerHTML='<option value=\"\">Request book...</option>';books.forEach(function(b){if((!t||b.tp===t)&&(!lv||b.l===lv)&&(!cl||b.c===cl)){sel.innerHTML+='<option value=\"'+b.i+'\">'+b.t+'</option>'}})}");
h.append("populateRequest()");
        
        h.append("</script></head><body><div class='container'>");

        h.append("<div class='topbar'><div class='brand'>&#128218; <span>e</span>Library</div><div><a href='/logout' class='btn-nav'>Logout</a></div></div>");
        h.append("<h1>My eLibrary</h1><p class='sub'>Welcome back, <strong>").append(username).append("</strong></p>");

        // Stats
        long myPendingReq = requests.stream().filter(r -> r.getUsername().equals(username) && "pending".equals(r.getStatus())).count();
        long myBorrowed = borrows.stream().filter(br -> br.username.equals(username)).count();
        h.append("<div class='stats'>");
        h.append("<div class='stat avail'><span class='n'>").append(avail).append("</span><span class='l'>Available</span></div>");
        h.append("<div class='stat req'><span class='n'>").append(myPendingReq).append("</span><span class='l'>Requests</span></div>");
        h.append("<div class='stat bor'><span class='n'>").append(myBorrowed).append("</span><span class='l'>Borrowed</span></div>");

        h.append("</div>");

        // Browse Books
        h.append("<div class='card'><h2>&#128214; Browse Books</h2>");
        h.append("<div class='filter-row'><select id='ftype' onchange='populateLevel()'><option value=''>Select type...</option>");
        h.append("<option value='Tie'>Tie</option>");
h.append("</select><select id='flevel' onchange='populateClass()' disabled><option value=''>Select level first...</option></select>");
h.append("<select id='fclass' onchange='filterBooks()' disabled><option value=''>Select class first...</option></select>");
h.append("<select name='isbn' id='reqisbn' form='reqform' required><option value=''>Request book...</option></select>");
h.append("<button type='submit' class='btn btn-pri' style='padding:10px 18px;font-size:12px' form='reqform'>Request</button></div>");
h.append("<form id='reqform' method='POST'></form>");
        h.append("<div class='table-wrap'><table><tr><th>ISBN</th><th>Title</th><th>Author</th></tr><tbody id='btable'>");
        h.append("<tr><td colspan='3' class='empty'>Select a filter above to browse books</td></tr>");
        h.append("</tbody></table></div></div>");

        // My Requests
        h.append("<div class='card'><h2>&#128203; My Requests</h2>");
        List<Request> myReqs = requests.stream().filter(r -> r.getUsername().equals(username) && "pending".equals(r.getStatus())).collect(Collectors.toList());
        if (!myReqs.isEmpty()) {
            h.append("<div class='table-wrap'><table><tr><th>Book</th><th>Author</th><th>Requested At</th><th>Action</th></tr>");
            for (Request r : myReqs) {
                Book b = findBook(r.getIsbn());
                String bookTitle = b != null ? b.getTitle() : r.getIsbn();
                String author = b != null ? b.getAuthor() : "";
                h.append("<tr><td data-label=\"Book\">").append(bookTitle).append("</td><td data-label=\"Author\">").append(author).append("</td><td data-label=\"Requested At\" class='sr'>").append(r.getTimestamp()).append("</td>")
                 .append("<td data-label=\"Action\"><form method='POST'><input type='hidden' name='action' value='cancel'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><button class='btn btn-rj btn-sm'>Cancel</button></form></td></tr>");
            }
            h.append("</table></div>");
        } else h.append("<p class='empty'>No requests yet.</p>");
        h.append("</div>");

        // My Borrowed Books
        h.append("<div class='card'><h2>&#128230; My Borrowed Books</h2>");
        List<BorrowRecord> myBorrows = borrows.stream().filter(br -> br.username.equals(username)).collect(Collectors.toList());
        if (!myBorrows.isEmpty()) {
            h.append("<div class='table-wrap'><table><tr><th>ISBN</th><th>Title</th><th>Author</th><th>Due Date</th><th>Status</th><th>Read</th></tr>");
            for (BorrowRecord br : myBorrows) {
                Book b = findBook(br.isbn);
                String title = b != null ? b.getTitle() : br.isbn;
                String author = b != null ? b.getAuthor() : "";
                long daysUntilDue = ChronoUnit.DAYS.between(LocalDateTime.now(), LocalDateTime.parse(br.dueDate, DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
                long daysOver = br.getDaysOverdue();
                String sc = daysUntilDue <= 0 ? "sb" : (daysUntilDue <= 2 ? "sy" : "sa");
                String st = daysOver > 0 ? daysOver + " day(s) overdue" : (daysUntilDue <= 0 ? "Due today" : (daysUntilDue <= 2 ? daysUntilDue + " day(s) remaining" : "On time"));
                String dueSc = daysUntilDue <= 0 ? "sb" : (daysUntilDue <= 2 ? "sy" : "");
                h.append("<tr><td data-label=\"ISBN\">").append(br.isbn).append("</td><td data-label=\"Title\">").append(title)
                 .append("</td><td data-label=\"Author\">").append(author).append("</td>")
                 .append("<td data-label=\"Due Date\" class='").append(dueSc).append("'>").append(br.dueDate).append("</td>")
                 .append("<td data-label=\"Status\" class='").append(sc).append("'>").append(st).append("</td>")
                 .append("<td data-label=\"Read\"><a href='/read?isbn=").append(br.isbn).append("' class='btn btn-pri btn-sm' style='text-decoration:none'>Read</a></td></tr>");
            }
            h.append("</table></div>");
            h.append("<div style='margin-top:16px;padding-top:16px;border-top:1px solid rgba(255,255,255,.06)'>");
            h.append("<p style='color:#c8c8e8;font-size:.9em;font-weight:600;margin-bottom:10px'>Return a Book</p>");
            h.append("<form method='POST' class='form-row'><input type='hidden' name='action' value='return'>");
            h.append("<div class='form-group'><select name='isbn' required><option value=''>Select borrowed book...</option>");
            for (BorrowRecord br : myBorrows) {
                Book b = findBook(br.isbn);
                String title = b != null ? b.getTitle() : br.isbn;
                h.append("<option value='").append(br.isbn).append("'>").append(title).append("</option>");
            }
            h.append("</select><button type='submit' class='btn btn-suc'>Return</button></form></div>");
        } else h.append("<p class='empty'>You have no borrowed books.</p>");
        h.append("<a href='https://wa.me/255656424007' target='_blank' class='wa-fab' title='Chat on WhatsApp'>&#128172;</a>");
        h.append("<div class='wa-tooltip'>Need help? Chat with us &#128172;</div>");
        h.append("</div></div></body></html>");
        return h.toString();
    }

    // ── Read Online ─────────────────────────────────────────

    private static void handleRead(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        if (user == null) { redirect(exchange, "/login"); return; }

        String query = exchange.getRequestURI().getQuery();
        String isbn = "";
        if (query != null) {
            for (String p : query.split("&")) {
                String[] kv = p.split("=", 2);
                if (kv.length == 2 && "isbn".equals(kv[0])) isbn = kv[1];
            }
        }
        if (isbn.isEmpty()) { redirect(exchange, "/dashboard"); return; }

        Book b = findBook(isbn);
        if (b == null) { sendHtml(exchange, "<html><body><h2>Book not found</h2><a href='/dashboard'>Back</a></body></html>"); return; }

        BorrowRecord activeBorrow = null;
        for (BorrowRecord br : borrows) {
            if (br.isbn.equals(isbn) && br.username.equals(user)) {
                activeBorrow = br;
                break;
            }
        }

        if (activeBorrow == null) {
            sendHtml(exchange, generateReadPage(b, null, true, "You have not borrowed this book."));
            return;
        }

        if (activeBorrow.isOverdue()) {
            sendHtml(exchange, generateReadPage(b, activeBorrow, true, "Your borrowing period has expired. Please return the book."));
            return;
        }

        String flipUrl = flipbooks.get(isbn);
        sendHtml(exchange, generateReadPage(b, activeBorrow, false, null, flipUrl));
    }

    private static String generateReadPage(Book book, BorrowRecord br, boolean blocked, String message) {
        return generateReadPage(book, br, blocked, message, null);
    }

    private static String generateReadPage(Book book, BorrowRecord br, boolean blocked, String message, String flipUrl) {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>").append(book.getTitle()).append(" &mdash; eLibrary System</title>");
        h.append("<link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&family=Merriweather:ital,wght@0,300;0,400;0,700;1,400&display=swap' rel='stylesheet'>");
        h.append("<style>");
        h.append("@keyframes fadeUp{0%{opacity:0;transform:translateY(30px)}100%{opacity:1;transform:translateY(0)}}");
        h.append("@keyframes fadeIn{0%{opacity:0}100%{opacity:1}}");
        h.append("@keyframes pageTurn{0%{opacity:0;transform:perspective(1200px) rotateY(-8deg) scale(.96)}100%{opacity:1;transform:perspective(1200px) rotateY(0) scale(1)}}");
        h.append("@keyframes glowPulse{0%{box-shadow:0 0 20px rgba(102,126,234,.15)}50%{box-shadow:0 0 40px rgba(102,126,234,.3)}100%{box-shadow:0 0 20px rgba(102,126,234,.15)}}");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Merriweather',Georgia,serif;min-height:100vh;position:relative;background:#0a0a18;overflow-x:hidden}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:radial-gradient(ellipse at 20% 50%,rgba(102,126,234,.06),transparent 60%),radial-gradient(ellipse at 80% 50%,rgba(118,75,162,.05),transparent 60%),url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=1600') center/cover no-repeat;filter:brightness(.06) blur(2px);pointer-events:none}");
        h.append(".topbar{position:relative;z-index:10;background:rgba(12,12,28,.95);backdrop-filter:blur(20px);border-bottom:1px solid rgba(255,255,255,.05);color:#e0e0f0;padding:0 30px;display:flex;justify-content:space-between;align-items:center;height:60px;animation:fadeIn .5s ease-out}");
        h.append(".topbar .title{font-family:'Inter',sans-serif;font-size:.9em;font-weight:600;color:rgba(255,255,255,.5);letter-spacing:.3px;max-width:50%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}");
        h.append(".topbar .title span{color:#667eea}");
        h.append(".topbar .nav-links{display:flex;gap:10px;align-items:center}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 18px;border:1px solid rgba(255,255,255,.06);border-radius:10px;font-size:.82em;font-family:'Inter',sans-serif;transition:all .3s;font-weight:500}");
        h.append(".topbar a:hover{color:#fff;border-color:#667eea;background:rgba(102,126,234,.1);transform:translateY(-1px)}");
        h.append(".topbar .nav-btn{padding:8px 20px;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border:none;font-weight:600;font-size:.82em}");
        h.append(".topbar .nav-btn:hover{transform:translateY(-1px);box-shadow:0 4px 20px rgba(102,126,234,.3);background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border-color:transparent}");
        h.append(".hero{position:relative;z-index:1;max-width:900px;margin:0 auto;padding:50px 24px 30px;text-align:center;animation:fadeUp .6s ease-out}");
        h.append(".hero .icon{font-size:56px;margin-bottom:16px;display:inline-block;filter:drop-shadow(0 8px 30px rgba(102,126,234,.25));animation:glowPulse 3s ease-in-out infinite}");
        h.append(".hero h1{font-family:'Inter',sans-serif;font-size:2.2em;color:#f0f0ff;font-weight:800;letter-spacing:-1px;line-height:1.2;margin-bottom:8px}");
        h.append(".hero .author{color:rgba(255,255,255,.35);font-size:1.1em;font-weight:400}.hero .author strong{color:rgba(255,255,255,.55);font-weight:600}");
        h.append(".hero .meta{display:flex;justify-content:center;gap:20px;margin-top:16px;flex-wrap:wrap}");
        h.append(".hero .meta span{font-family:'Inter',sans-serif;font-size:.78em;color:rgba(255,255,255,.25);background:rgba(255,255,255,.03);border:1px solid rgba(255,255,255,.04);padding:6px 16px;border-radius:20px;letter-spacing:.3px}");
        h.append(".due-bar{position:relative;z-index:1;max-width:800px;margin:0 auto 20px;padding:0 24px;animation:fadeUp .6s ease-out .1s both}");
        h.append(".due-bar .inner{background:rgba(12,12,28,.85);backdrop-filter:blur(14px);border:1px solid rgba(255,255,255,.06);border-radius:16px;padding:18px 28px;display:flex;justify-content:space-between;align-items:center;transition:all .3s}");
        h.append(".due-bar .inner:hover{border-color:rgba(255,255,255,.1)}");
        h.append(".due-bar .warn{color:#ff6b6b;font-family:'Inter',sans-serif;font-weight:600;font-size:.9em;display:flex;align-items:center;gap:8px}.due-bar .warn-yellow{color:#f1c40f;font-family:'Inter',sans-serif;font-weight:600;font-size:.9em;display:flex;align-items:center;gap:8px}");
        h.append(".due-bar .warn::before,.due-bar .warn-yellow::before{content:'\\26A0';font-size:1.1em}");
        h.append(".due-bar .ok{color:#2ecc71;font-family:'Inter',sans-serif;font-weight:600;font-size:.9em;display:flex;align-items:center;gap:8px}");
        h.append(".due-bar .ok::before{content:'\\2713';font-size:1.1em}");
        h.append(".due-bar .date{color:rgba(255,255,255,.3);font-family:'Inter',sans-serif;font-size:.82em}");
        h.append(".reader{position:relative;z-index:1;max-width:860px;margin:0 auto;padding:0 24px 60px;animation:fadeUp .6s ease-out .15s both}");
        h.append(".flipframe{width:100%;height:calc(100vh - 160px);border-radius:16px;overflow:hidden;box-shadow:0 8px 60px rgba(0,0,0,.4);border:1px solid rgba(255,255,255,.04)}");
        h.append(".flipframe iframe{width:100%;height:100%;border:none}");
        h.append(".content-card{background:rgba(16,16,36,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 55px;box-shadow:0 8px 60px rgba(0,0,0,.3);line-height:2;font-size:1.05em;color:rgba(255,255,255,.8);animation:pageTurn .8s ease-out;position:relative}");
        h.append(".content-card::before{content:'';position:absolute;top:0;left:0;right:0;height:3px;background:linear-gradient(90deg,#667eea,#764ba2,transparent);border-radius:20px 20px 0 0}");
        h.append(".content-card p{margin-bottom:1.2em;text-indent:1.5em}");
        h.append(".content-card strong{color:#d0d0ff;font-weight:700}");
        h.append(".blocked{text-align:center;padding:120px 24px;position:relative;z-index:1;max-width:600px;margin:0 auto;animation:fadeUp .6s ease-out}");
        h.append(".blocked .icon{font-size:72px;margin-bottom:20px;display:block;opacity:.5}");
        h.append(".blocked h2{font-family:'Inter',sans-serif;color:#ff6b6b;margin-bottom:12px;font-size:1.5em;font-weight:700}");
        h.append(".blocked p{color:rgba(255,255,255,.4);margin-bottom:32px;font-size:.95em;line-height:1.6}");
        h.append(".blocked a{display:inline-block;padding:14px 36px;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border-radius:12px;text-decoration:none;font-family:'Inter',sans-serif;font-weight:600;font-size:.9em;transition:all .3s}");
        h.append(".blocked a:hover{transform:translateY(-3px);box-shadow:0 12px 35px rgba(102,126,234,.35)}");
        h.append(".reader-foot{text-align:center;margin-top:24px;padding-bottom:20px}");
        h.append(".back-btn{display:inline-flex;align-items:center;gap:8px;padding:14px 36px;border:none;border-radius:12px;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);text-decoration:none;font-family:'Inter',sans-serif;transition:all .3s;letter-spacing:.3px}");
        h.append(".back-btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}");
        h.append(".back-btn:active{transform:translateY(0)}");
        h.append("@media(max-width:600px){.hero h1{font-size:1.4em}.content-card{padding:28px 22px;font-size:.95em}.topbar{padding:0 16px}.due-bar .inner{flex-direction:column;gap:8px;text-align:center}}");
        h.append("</style></head><body>");
        h.append("<div class='topbar'><div class='title'>&#128218; <span>e</span>Library</div><div class='nav-links'><a href='/dashboard' class='nav-btn'>&larr; Dashboard</a></div></div>");
        h.append("<div class='hero'><div class='icon'>&#128214;</div><h1>").append(book.getTitle()).append("</h1>");
        h.append("<p class='author'>by <strong>").append(book.getAuthor()).append("</strong></p>");
        h.append("<div class='meta'><span>").append(book.getType()).append("</span><span>").append(book.getLevel()).append("</span><span>").append(book.getClassName()).append("</span></div></div>");

        if (br != null) {
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDateTime.now(), LocalDateTime.parse(br.dueDate, DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
            String warnClass = daysUntilDue <= 0 ? "warn" : (daysUntilDue <= 2 ? "warn-yellow" : "ok");
            String warnText = daysUntilDue <= 0 ? "Overdue — please return the book" : (daysUntilDue <= 2 ? daysUntilDue + " day(s) remaining" : "On time");
            h.append("<div class='due-bar'><div class='inner'><span class='").append(warnClass).append("'>").append(warnText).append("</span><span class='date'>Due ").append(br.dueDate).append("</span></div></div>");
        }

        h.append("<div class='reader'>");
        if (blocked) {
            h.append("</div>");
            h.append("<div class='blocked'><div class='icon'>&#128274;</div><h2>").append(message != null ? message : "Access Denied").append("</h2><p>You have not borrowed this book or your borrowing period has expired.</p><a href='/dashboard'>Back to Dashboard</a></div>");
        } else if (flipUrl != null) {
            h.append("<div class='flipframe'><iframe src='").append(flipUrl).append("' allowfullscreen></iframe></div><div class='reader-foot'><a href='/dashboard' class='back-btn'>&larr; Back to Dashboard</a></div></div>");
        } else {
            h.append("<div class='content-card'>").append(book.getContent()).append("</div><div class='reader-foot'><a href='/dashboard' class='back-btn'>&larr; Back to Dashboard</a></div></div>");
        }
        h.append("</body></html>");
        return h.toString();
    }

    private static void handleAdmin(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        boolean isAdmin = user != null && "admin".equals(getRole(user));

        cleanExpiredBorrows();

        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String action = form.getOrDefault("action", "");
            String isbn = form.getOrDefault("isbn", "");
            String reqUser = form.getOrDefault("username", "");

            if ("login".equals(action)) {
                String u = form.getOrDefault("username", "");
                String p = form.getOrDefault("password", "");
                for (User us : users) {
                    if (us.getUsername().equals(u) && us.getPassword().equals(p) && "admin".equals(us.getRole())) {
                        setSession(exchange, u);
                        redirect(exchange, "/admin");
                        return;
                    }
                }
                redirect(exchange, "/admin?failed=1");
                return;
            }

            if (!isAdmin) { redirect(exchange, "/admin"); return; }

            switch (action) {
                case "approve" -> {
                    if (findBook(isbn) != null) {
                        for (Request r : requests) {
                            if (r.getIsbn().equals(isbn) && r.getUsername().equals(reqUser) && "pending".equals(r.getStatus())) {
                                borrows.add(new BorrowRecord(isbn, reqUser));
                                r.setStatus("approved");
                                saveBorrows();
                                saveRequests();
                                break;
                            }
                        }
                    }
                }
                case "reject" -> {
                    for (Request r : requests) {
                        if (r.getIsbn().equals(isbn) && r.getUsername().equals(reqUser) && "pending".equals(r.getStatus())) {
                            r.setStatus("rejected");
                            saveRequests();
                            break;
                        }
                    }
                }
                case "deleteuser" -> {
                    User toRemove = null;
                    for (User u : users) {
                        if (u.getUsername().equals(reqUser) && !"admin".equals(u.getRole())) {
                            toRemove = u;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        users.remove(toRemove);
                        requests.removeIf(r -> r.getUsername().equals(reqUser));
                        borrows.removeIf(br -> br.username.equals(reqUser));
                        saveUsers();
                        saveRequests();
                        saveBorrows();
                    }
                }
                case "updatesettings" -> {
                    String daysStr = form.getOrDefault("borrowDays", "7");
                    try {
                        int days = Integer.parseInt(daysStr);
                        if (days >= 1 && days <= 90) BORROW_DAYS = days;
                    } catch (NumberFormatException e) {}
                }
            }
            redirect(exchange, "/admin");
            return;
        }

        if (isAdmin) sendHtml(exchange, generateAdminPage());
        else {
            String q = exchange.getRequestURI().getQuery();
            sendHtml(exchange, generateAdminLogin(q != null && q.contains("failed=1")));
        }
    }

    private static String generateAdminLogin(boolean failed) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Librarian Login &mdash; eLibrary System</title>
            <style>
            *{box-sizing:border-box;margin:0;padding:0}
            body{font-family:'Inter','Segoe UI',sans-serif;background:#0b0b1a;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:20px;position:relative;overflow:hidden}
            body::before{content:'';position:fixed;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(ellipse at 30% 20%,rgba(231,76,60,.1) 0%,transparent 50%),radial-gradient(ellipse at 70% 80%,rgba(192,57,43,.08) 0%,transparent 50%);pointer-events:none}
            .box{background:rgba(18,18,40,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 40px;box-shadow:0 25px 80px rgba(0,0,0,.6),inset 0 1px 0 rgba(255,255,255,.05);width:100%;max-width:400px;text-align:center;position:relative}
            .box::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:60%;height:2px;background:linear-gradient(90deg,transparent,#e74c3c,#c0392b,transparent)}
            .logo{font-size:44px;margin-bottom:12px;display:block;filter:drop-shadow(0 4px 12px rgba(231,76,60,.3))}
            h2{color:#e8e8ff;margin-bottom:4px;font-size:1.5em;font-weight:700;letter-spacing:-.5px}
            .sub{color:rgba(255,255,255,.4);font-size:.9em;margin-bottom:28px}
            .input-group{position:relative;margin-bottom:14px}
            .input-group .icon{position:absolute;left:16px;top:50%;transform:translateY(-50%);color:rgba(255,255,255,.25);font-size:16px;pointer-events:none}
            .input-group input{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none}
            .input-group input::placeholder{color:rgba(255,255,255,.2)}
            .input-group input:focus{border-color:rgba(231,76,60,.5);background:rgba(231,76,60,.06);box-shadow:0 0 0 3px rgba(231,76,60,.1)}
            .btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#e74c3c 0%,#c0392b 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}
            .btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(231,76,60,.35)}
            .btn:active{transform:translateY(0)}
            .err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:12px 16px;border-radius:10px;margin-bottom:18px;font-size:.85em;text-align:left}
            .link{display:block;margin-top:18px;color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}
            .link:hover{color:#e74c3c}
            .input-group{position:relative}
            .toggle-pwd{position:absolute;right:16px;top:50%;transform:translateY(-50%);cursor:pointer;color:rgba(255,255,255,.35);font-size:18px;user-select:none;z-index:2}
            .toggle-pwd:hover{color:rgba(255,255,255,.6)}
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style>
            <script>function togglePwd(id,el){var inp=document.getElementById(id);if(inp.type==='password'){inp.type='text';el.innerHTML='&#128064;'}else{inp.type='password';el.innerHTML='&#128065;'}}</script>
            </head><body>
            <div class="box">
            <span class="logo">&#128274;</span>
            <h2>Librarian Login</h2>
            <p class='sub'>Staff access only</p>""" + (failed ? "<div class='err'>Invalid credentials</div>" : "") + """
            <form method='POST'>
            <input type='hidden' name='action' value='login'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Librarian username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' id='apwd' placeholder='Password' required><span class='toggle-pwd' onclick="togglePwd('apwd',this)">&#128065;</span></div>
            <button type='submit' class='btn'>Login</button>
            </form>
            <a href='/login' class='link'>&larr; Back to eLibrary</a>
            </div></body></html>
            """;
    }

    private static String generateAdminPage() {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>Librarian Panel &mdash; eLibrary System</title><style>");
        h.append("@keyframes fadeUp{0%{opacity:0;transform:translateY(30px)}100%{opacity:1;transform:translateY(0)}}");
        h.append("@keyframes fadeIn{0%{opacity:0}100%{opacity:1}}");
        h.append("@keyframes slideIn{0%{opacity:0;transform:translateX(-20px)}100%{opacity:1;transform:translateX(0)}}");
        h.append("@keyframes glowPulse{0%{box-shadow:0 0 5px rgba(231,76,60,.2)}50%{box-shadow:0 0 25px rgba(231,76,60,.4)}100%{box-shadow:0 0 5px rgba(231,76,60,.2)}}");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Inter','Segoe UI',sans-serif;min-height:100vh;padding:20px;position:relative;overflow-x:hidden}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=1600') center/cover no-repeat;filter:brightness(.08) blur(3px);pointer-events:none}");
        h.append(".container{max-width:1100px;margin:0 auto;position:relative;z-index:1;animation:fadeUp .6s ease-out}");
        h.append(".topbar{display:flex;justify-content:space-between;align-items:center;padding:16px 28px;background:rgba(16,16,36,.8);border:1px solid rgba(255,255,255,.06);border-radius:16px;margin-bottom:28px;backdrop-filter:blur(14px);animation:fadeIn .8s ease-out}");
        h.append(".topbar .brand{color:#e0e0f0;font-size:1.05em;font-weight:600;letter-spacing:.3px;display:flex;align-items:center;gap:8px}");
        h.append(".topbar .brand span{color:#e74c3c}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 20px;border:1px solid rgba(255,255,255,.08);border-radius:10px;font-size:.88em;transition:all .3s;position:relative;overflow:hidden}");
        h.append(".topbar a::before{content:'';position:absolute;inset:0;background:linear-gradient(135deg,#e74c3c,#c0392b);opacity:0;transition:opacity .3s;border-radius:10px}");
        h.append(".topbar a:hover::before{opacity:.15}");
        h.append(".topbar a:hover{color:#fff;border-color:#e74c3c;transform:translateY(-2px);box-shadow:0 6px 20px rgba(231,76,60,.2)}");
        h.append("h1{color:#e8e8ff;text-align:center;margin-bottom:4px;font-size:1.8em;font-weight:700;letter-spacing:-.5px;animation:fadeIn .6s ease-out .1s both}.sub{color:rgba(255,255,255,.35);text-align:center;margin-bottom:28px;font-size:.95em;animation:fadeIn .6s ease-out .15s both}");
        h.append(".stats{display:flex;gap:14px;margin-bottom:28px;flex-wrap:wrap}");
        h.append(".stat{padding:20px 24px;flex:1;min-width:130px;text-align:center;border-radius:14px;border:1px solid rgba(255,255,255,.06);transition:all .4s cubic-bezier(.25,.46,.45,.94);position:relative;overflow:hidden}");
        h.append(".stat::after{content:'';position:absolute;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(circle,var(--glow,transparent) 0%,transparent 70%);opacity:0;transition:opacity .4s}");
        h.append(".stat:hover::after{opacity:1}");
        h.append(".stat .n{font-size:2em;font-weight:700;display:block;position:relative;z-index:1}.stat .l{font-size:.82em;margin-top:5px;opacity:.85;text-transform:uppercase;letter-spacing:.5px;position:relative;z-index:1}");
        h.append(".stat.avail{background:rgba(39,174,96,.1);color:#2ecc71;border-color:rgba(39,174,96,.15);--glow:rgba(39,174,96,.15)}");
        h.append(".stat.avail:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(39,174,96,.2);border-color:rgba(39,174,96,.3)}");
        h.append(".stat.req{background:rgba(243,156,18,.08);color:#f1c40f;border-color:rgba(243,156,18,.12);--glow:rgba(243,156,18,.12)}");
        h.append(".stat.req:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(243,156,18,.2);border-color:rgba(243,156,18,.3)}");
        h.append(".stat.bor{background:rgba(231,76,60,.08);color:#e74c3c;border-color:rgba(231,76,60,.12);--glow:rgba(231,76,60,.12)}");
        h.append(".stat.bor:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(231,76,60,.2);border-color:rgba(231,76,60,.3)}");
        h.append(".stat.tot{background:rgba(39,174,96,.08);color:#2ecc71;border-color:rgba(39,174,96,.12);--glow:rgba(39,174,96,.1)}");
        h.append(".stat.tot:hover{transform:translateY(-6px);box-shadow:0 12px 40px rgba(39,174,96,.2);border-color:rgba(39,174,96,.3)}");
        h.append(".card{background:rgba(16,16,36,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:18px;padding:28px;margin-bottom:24px;box-shadow:0 8px 50px rgba(0,0,0,.3);transition:all .4s cubic-bezier(.25,.46,.45,.94);animation:fadeUp .6s ease-out .2s both}");
        h.append(".card:hover{border-color:rgba(255,255,255,.1);box-shadow:0 12px 60px rgba(0,0,0,.4);transform:translateY(-2px)}");
        h.append(".card h2{font-size:1.1em;color:#d0d0f0;margin-bottom:16px;display:flex;align-items:center;gap:10px;font-weight:600;letter-spacing:.2px}");
        h.append("table{width:100%;border-collapse:collapse}");
        h.append("th{padding:14px 12px;text-align:left;font-size:.75em;text-transform:uppercase;letter-spacing:.7px;color:rgba(255,255,255,.3);font-weight:700;border-bottom:1px solid rgba(255,255,255,.06)}");
        h.append("td{padding:14px 12px;border-bottom:1px solid rgba(255,255,255,.04);font-size:.9em;color:rgba(255,255,255,.65);transition:all .3s}");
        h.append("tr{transition:background .2s}");
        h.append("tr:nth-child(even):not(.group-header) td{background:rgba(255,255,255,.015)}");
        h.append("tr:hover td{color:rgba(255,255,255,.9);background:rgba(231,76,60,.06)}");
        h.append("tr.group-header td{padding:10px 12px;background:rgba(231,76,60,.12);color:#e74c3c;font-weight:700;font-size:.82em;text-transform:uppercase;letter-spacing:1px;border-bottom:1px solid rgba(231,76,60,.15);cursor:default}");
        h.append("tr:last-child td{border-bottom:none}");
        h.append(".sa{color:#2ecc71;font-weight:600;text-shadow:0 0 20px rgba(46,204,113,.15)}.sb{color:#e74c3c;font-weight:600}.sy{color:#f1c40f;font-weight:600;text-shadow:0 0 20px rgba(241,196,15,.15)}");
        h.append(".pending{display:flex;justify-content:space-between;align-items:center;padding:18px 22px;background:rgba(255,255,255,.02);border:1px solid rgba(255,255,255,.06);border-radius:14px;margin-bottom:12px;flex-wrap:wrap;gap:14px;transition:all .4s cubic-bezier(.25,.46,.45,.94);animation:slideIn .4s ease-out both}");
        h.append(".pending:nth-child(2){animation-delay:.05s}.pending:nth-child(3){animation-delay:.1s}.pending:nth-child(4){animation-delay:.15s}");
        h.append(".pending:hover{border-color:rgba(255,255,255,.1);background:rgba(255,255,255,.04);transform:translateX(4px)}");
        h.append(".pending .info{flex:1;color:rgba(255,255,255,.6);font-size:.92em}.pending .info strong{color:#e0e0f0}");
        h.append(".pending .btns{display:flex;gap:10px}");
        h.append("form{display:flex;gap:10px;flex-wrap:wrap;align-items:center}");
        h.append(".btn{padding:9px 20px;border:none;border-radius:10px;cursor:pointer;font-size:13px;font-weight:600;color:#fff;transition:all .3s cubic-bezier(.25,.46,.45,.94);letter-spacing:.2px;position:relative;overflow:hidden}");
        h.append(".btn::after{content:'';position:absolute;inset:0;background:linear-gradient(135deg,transparent 30%,rgba(255,255,255,.1) 50%,transparent 70%);background-size:200% 100%;transition:background .5s}");
        h.append(".btn:hover::after{background-position:100% 0}");
        h.append(".btn:hover{transform:translateY(-3px)}");
        h.append(".btn:active{transform:translateY(-1px)}");
        h.append(".btn-ap{background:linear-gradient(135deg,#27ae60,#2ecc71);box-shadow:0 4px 15px rgba(39,174,96,.15)}");
        h.append(".btn-ap:hover{box-shadow:0 8px 25px rgba(39,174,96,.3)}");
        h.append(".btn-rj{background:linear-gradient(135deg,#e74c3c,#ff6b6b);box-shadow:0 4px 15px rgba(231,76,60,.15)}");
        h.append(".btn-rj:hover{box-shadow:0 8px 25px rgba(231,76,60,.3)}");
        h.append(".btn-dl{background:#e74c3c;padding:4px 12px;font-size:12px}");
        h.append(".topbar .btn-nav{background:linear-gradient(135deg,#e74c3c,#c0392b);color:#fff;border:none;padding:8px 22px;text-decoration:none;font-size:.88em;font-weight:600;border-radius:10px;transition:all .3s cubic-bezier(.25,.46,.45,.94);display:inline-block}");
        h.append(".topbar .btn-nav:hover{transform:translateY(-2px);box-shadow:0 8px 25px rgba(231,76,60,.35);color:#fff}");
        h.append(".empty{text-align:center;padding:30px;color:rgba(255,255,255,.25);font-size:.92em}");
        h.append(".btn-wa{background:#25D366!important;color:#fff!important;box-shadow:0 4px 15px rgba(37,211,102,.15)!important}");
        h.append(".btn-wa:hover{box-shadow:0 8px 25px rgba(37,211,102,.3)!important}");
        h.append("@media(max-width:600px){body{padding:12px}.pending{flex-direction:column;animation-delay:0s!important}.pending .btns{width:100%}}");
        h.append("</style></head><body><div class='container'>");

        h.append("<div class='topbar'><div class='brand'>&#128274; <span>Librarian</span> Panel</div><div style='display:flex;gap:6px;align-items:center'><a href='https://wa.me/255656424007?text=Hi%20Librarian%2C%20I%20need%20help' target='_blank' class='btn-nav btn-wa' style='text-decoration:none;font-size:12px;padding:6px 14px'>&#128242; WhatsApp</a><a href='/logout' class='btn-nav'>Logout</a></div></div>");
        h.append("<h1>Librarian Panel</h1><p class='sub'>Manage book requests &amp; users</p>");

        // Stats row
        long totalReq = requests.stream().filter(r -> "pending".equals(r.getStatus())).count();
        h.append("<div class='stats'>");
        h.append("<div class='stat avail'><span class='n'>").append(library.size()).append("</span><span class='l'>Available</span></div>");
        h.append("<div class='stat req'><span class='n'>").append(totalReq).append("</span><span class='l'>Requests</span></div>");
        h.append("<div class='stat bor'><span class='n'>").append(borrows.size()).append("</span><span class='l'>Borrowed</span></div>");
        h.append("<div class='stat tot'><span class='n'>").append(users.size()).append("</span><span class='l'>Users</span></div>");
        h.append("</div>");

        // Pending requests grouped by user
        h.append("<div class='card'><h2>&#128203; Pending Requests</h2>");
        List<Request> pending = requests.stream().filter(r -> "pending".equals(r.getStatus())).collect(Collectors.toList());
        if (!pending.isEmpty()) {
            Map<String, List<Request>> byUser = new LinkedHashMap<>();
            for (Request r : pending)
                byUser.computeIfAbsent(r.getUsername(), k -> new ArrayList<>()).add(r);
            for (Map.Entry<String, List<Request>> entry : byUser.entrySet()) {
                h.append("<h3 style='color:#e8e8ff;font-size:.95em;margin:4px 0 10px 8px;opacity:.9'>").append(entry.getKey()).append("</h3>");
                for (Request r : entry.getValue()) {
                    Book b = findBook(r.getIsbn());
                    String title = b != null ? b.getTitle() : r.getIsbn();
                    h.append("<div class='pending'><div class='info'><strong>").append(title).append("</strong>")
                     .append(" &mdash; ").append(r.getTimestamp())
                     .append("</div><div class='btns'>")
                     .append("<form method='POST'><input type='hidden' name='action' value='approve'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><input type='hidden' name='username' value='").append(r.getUsername()).append("'><button class='btn btn-ap'>Approve</button></form>")
                     .append("<form method='POST'><input type='hidden' name='action' value='reject'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><input type='hidden' name='username' value='").append(r.getUsername()).append("'><button class='btn btn-rj'>Reject</button></form>")
                     .append("</div></div>");
                }
            }
        } else h.append("<p class='empty'>No pending requests.</p>");
        h.append("</div>");

        // Manage users
        h.append("<div class='card'><h2>&#128101; Manage Users</h2>");
        h.append("<table><tr><th>Username</th><th>Phone</th><th>Role</th><th>Action</th></tr>");
        for (User u : users) {
            String phone = u.getPhoneNumber();
            h.append("<tr><td>").append(u.getUsername()).append("</td><td>").append(phone.isEmpty() ? "&mdash;" : phone).append("</td><td>").append(u.getRole()).append("</td><td>");
            if (!"admin".equals(u.getRole()))
                h.append("<form method='POST'><input type='hidden' name='action' value='deleteuser'><input type='hidden' name='username' value='").append(u.getUsername()).append("'><button class='btn btn-dl'>Delete</button></form>");
            else h.append("<span style='color:#aaa;font-size:.85em'>-</span>");
            h.append("</td></tr>");
        }
        h.append("</table></div>");

        // Borrowed books grouped by user
        h.append("<div class='card'><h2>&#128200; Borrowed Books</h2>");
        if (!borrows.isEmpty()) {
            Map<String, List<BorrowRecord>> byUser = new LinkedHashMap<>();
            for (BorrowRecord br : borrows)
                byUser.computeIfAbsent(br.username, k -> new ArrayList<>()).add(br);
            h.append("<table><tr><th>Borrower</th><th>ISBN</th><th>Title</th><th>Due Date</th><th>Status</th></tr>");
            for (Map.Entry<String, List<BorrowRecord>> entry : byUser.entrySet()) {
                h.append("<tr class='group-header'><td colspan='5'>").append(entry.getKey()).append("</td></tr>");
                for (BorrowRecord br : entry.getValue()) {
                    Book b = findBook(br.isbn);
                    String title = b != null ? b.getTitle() : br.isbn;
                    long daysUntilDue = ChronoUnit.DAYS.between(LocalDateTime.now(), LocalDateTime.parse(br.dueDate, DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
                    long d = br.getDaysOverdue();
                    String sc = daysUntilDue <= 0 ? "sb" : (daysUntilDue <= 2 ? "sy" : "sa");
                    String st = d > 0 ? d + " day(s) overdue" : (daysUntilDue <= 0 ? "Due today" : (daysUntilDue <= 2 ? daysUntilDue + " day(s) remaining" : "On time"));
                    h.append("<tr><td></td><td>").append(br.isbn).append("</td><td>").append(title)
                     .append("</td><td>").append(br.dueDate).append("</td>")
                     .append("<td class='").append(sc).append("'>").append(st).append("</td></tr>");
                }
            }
            h.append("</table>");
        } else h.append("<p class='empty'>No borrowed books.</p>");
        h.append("</div>");

        // Settings
        h.append("<div class='card'><h2>&#128295; Settings</h2>");
        h.append("<form method='POST' class='form-row' style='display:flex;gap:10px;align-items:end;flex-wrap:wrap'>");
        h.append("<input type='hidden' name='action' value='updatesettings'>");
        h.append("<div class='form-group'><label style='font-size:.85em;color:#aaa;display:block;margin-bottom:4px'>Borrow Duration (days)</label>");
        h.append("<input type='number' name='borrowDays' value='").append(Main.BORROW_DAYS).append("' min='1' max='90' style='padding:8px 12px;border-radius:8px;border:1px solid rgba(255,255,255,.1);background:rgba(255,255,255,.05);color:#fff;font-size:.9em'>");
        h.append("</div><button type='submit' class='btn btn-ap'>Save Settings</button></form>");
        h.append("</div>");

        // All books grouped by class
        h.append("<div class='card'><h2>&#128214; All Books by Class</h2>");
        Map<String, List<Book>> byClass = new LinkedHashMap<>();
        List<String> classOrder = Arrays.asList("Nursery", "Standard 1", "Standard 2", "Standard 3", "Standard 4", "Standard 5", "Standard 6", "Standard 7", "Form 1", "Form 2", "Form 3", "Form 4", "Form 5", "Form 6", "Play", "Novel");
        for (Book b : library) {
            byClass.computeIfAbsent(b.getClassName(), k -> new ArrayList<>()).add(b);
        }
        List<Map.Entry<String, List<Book>>> sortedEntries = new ArrayList<>();
        for (String cls : classOrder) {
            if (byClass.containsKey(cls))
                sortedEntries.add(Map.entry(cls, byClass.remove(cls)));
        }
        byClass.forEach((k, v) -> sortedEntries.add(Map.entry(k, v)));
        h.append("<table><tr><th>ISBN</th><th>Title</th><th>Author</th><th>Class</th></tr>");
        for (Map.Entry<String, List<Book>> entry : sortedEntries) {
            h.append("<tr class='group-header'><td colspan='4'>").append(entry.getKey()).append("</td></tr>");
            for (Book b : entry.getValue()) {
                h.append("<tr><td>").append(b.getIsbn()).append("</td><td>").append(b.getTitle())
                 .append("</td><td>").append(b.getAuthor()).append("</td><td>").append(b.getClassName()).append("</td></tr>");
            }
        }
        h.append("</table></div></div></body></html>");
        return h.toString();
    }
}
