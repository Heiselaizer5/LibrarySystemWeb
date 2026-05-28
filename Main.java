import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Main {
    private static final List<Book> library = new ArrayList<>();
    private static final List<User> users = new ArrayList<>();
    private static final List<Request> requests = new ArrayList<>();
    private static final List<BorrowRecord> borrows = new ArrayList<>();
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();
    private static final Map<String, String> flipbooks = new HashMap<>();

    private static final Path DATA_FILE = Paths.get("users.dat");

    public static void main(String[] args) throws Exception {
        // ── Books ───────────────────────────────────────────
        library.add(Book.load("Advanced Mathematics", "TIE", "T009", "Tie", "A-Level", "Form 5",
            "Advanced mathematics covers calculus, complex numbers, and advanced statistics.<br><br>Chapter 1: Differentiation<br>Chapter 2: Integration<br>Chapter 3: Complex Numbers<br>Chapter 4: Vectors<br>Chapter 5: Probability Distributions"));
        library.add(Book.load("A River Between", "Ngugi wa Thiong'o", "N001", "Novel", "Secondary", "Form 3",
            "The river was the soul of the land. It separated the two ridges, Kameno and Makuyu, each holding onto ancient traditions.<br><br>Waiyaki grew up knowing the river was more than water. It was a boundary between two worlds. As he learned the ways of the white man, he dreamed of unity — of building a bridge across the river that divided his people.<br><br>But love and duty pulled him in different directions. Would he betray his heritage or abandon his vision for a new future?"));
        library.add(Book.load("Weep Not Child", "Ngugi wa Thiong'o", "N002", "Novel", "Secondary", "Form 2",
            "Njoroge sat under the mugumo tree, the weight of his family's hopes on his young shoulders. He was the first in his family to attend school, and education was their only escape from poverty.<br><br>But the Mau Mau uprising tore through the land like a wildfire. Brother turned against brother, and the soil ran red with blood. Njoroge learned that some battles cannot be won with books alone."));
        library.add(Book.load("The Lion and the Jewel", "Wole Soyinka", "P001", "Play", "Secondary", "Form 4",
            "The village of Ilujinle wakes to the sound of drums. Baroka, the aging Bale, seeks a new wife — the beautiful Sidi, known as the Jewel. But Lakunle, the young western-educated schoolteacher, also wants Sidi's hand.<br><br>What follows is a battle of tradition versus modernity, wit versus pride, and age versus youth. Who will win the Jewel?<br><br>ACT I: Morning<br>ACT II: Midday<br>ACT III: Evening"));
        library.add(Book.load("Three Suitors One Husband", "Moliere", "P002", "Play", "Secondary", "Form 1",
            "A comedy of errors unfolds when a young woman finds herself pursued by three very different suitors. Her father has chosen one, her mother another, but her heart belongs to a third.<br><br>Through witty dialogue and hilarious situations, this play explores love, family, and the timeless battle between arranged marriages and true love."));
        library.add(Book.load("Oxford English Dictionary", "Oxford Press", "R001", "Reference", "All", "All",
            "The definitive record of the English language. With over 600,000 words, phrases, and definitions, this comprehensive dictionary is an essential reference for students, writers, and language lovers.<br><br>Features:<br>- Complete A-Z coverage<br>- Etymology and word origins<br>- Pronunciation guides<br>- Usage examples<br>- Synonyms and antonyms"));
        library.add(Book.load("Encyclopedia of Science", "DK Publishing", "R002", "Reference", "All", "All",
            "A comprehensive guide to the world of science. From the tiniest atoms to the vast universe, this encyclopedia covers every branch of science with stunning illustrations and clear explanations.<br><br>Sections: Physics, Chemistry, Biology, Earth Science, Astronomy, Technology"));
        library.add(Book.load("The Holy Bible", "Various", "G001", "Religion", "All", "All",
            "The sacred scripture of Christianity, the Bible is divided into the Old and New Testaments. It contains 66 books written over thousands of years, telling the story of God's relationship with humanity.<br><br>Key Books: Genesis, Psalms, Proverbs, Gospels of Matthew, Mark, Luke, John"));

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
        library.add(Book.load("Practical Geography", "TIE", "T014p", "Tie", "A-Level", "Form 5",
            "Practical Geography Form 5 covers map reading, interpretation, and field work techniques. This supplementary guide provides hands-on exercises for mastering topographic maps, statistical diagrams, photograph interpretation, and research methodology in geography.<br><br>Topics: Map Reading and Interpretation, Statistical Methods and Diagrams, Photograph Interpretation, Field Research Techniques, Surveying and Sketching, Climate Data Analysis, Population Data Analysis, Environmental Impact Assessment."));
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

        // ── Flipbook URLs (TIE self-hosted, fliphtml5 fallback) ──
        flipbooks.put("T009", "https://online.fliphtml5.com/ebxst/ohok/");
        flipbooks.put("T012", "https://ol.tie.go.tz/uploaded_files/books//adv_secondary/frmv/Stud_Book/Biology/Biology_F5.html");
        flipbooks.put("T013", "https://online.fliphtml5.com/ebxst/qdgm/");
        flipbooks.put("T014", "https://online.fliphtml5.com/ebxst/tmhq/");
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

        users.add(new User("admin", "admin123", "admin"));
        loadUsers();

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> { try { handleRoot(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/signup", exchange -> { try { handleSignup(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/login", exchange -> { try { handleLogin(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/logout", exchange -> { try { handleLogout(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/dashboard", exchange -> { try { handleDashboard(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/admin", exchange -> { try { handleAdmin(exchange); } catch (Exception e) { e.printStackTrace(); }});
        server.createContext("/read", exchange -> { try { handleRead(exchange); } catch (Exception e) { e.printStackTrace(); }});

        System.out.println("Server: http://localhost:8080");
        System.out.println("LAN:    http://" + java.net.InetAddress.getLocalHost().getHostAddress() + ":8080");
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
        if (user == null) { redirect(exchange, "/login"); return; }
        if ("admin".equals(getRole(user))) { redirect(exchange, "/admin"); return; }
        redirect(exchange, "/dashboard");
    }

    // ── Signup ─────────────────────────────────────────────

    private static void handleSignup(HttpExchange exchange) throws Exception {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String username = form.getOrDefault("username", "").trim();
            String password = form.getOrDefault("password", "").trim();
            if (username.isEmpty() || password.isEmpty()) {
                sendHtml(exchange, generateSignupPage("Fill all fields."));
                return;
            }
            for (User u : users)
                if (u.getUsername().equals(username)) {
                    sendHtml(exchange, generateSignupPage("Username already taken."));
                    return;
                }
            users.add(new User(username, password, "user"));
            saveUsers();
            setSession(exchange, username);
            redirect(exchange, "/dashboard");
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
            .box{background:rgba(18,18,40,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:20px;padding:50px 40px;box-shadow:0 25px 80px rgba(0,0,0,.6),inset 0 1px 0 rgba(255,255,255,.05);width:100%;max-width:400px;text-align:center;position:relative;z-index:1}}
            .box::before{content:'';position:absolute;top:0;left:50%;transform:translateX(-50%);width:60%;height:2px;background:linear-gradient(90deg,transparent,#667eea,#764ba2,transparent)}
            .logo{font-size:44px;margin-bottom:12px;display:block;filter:drop-shadow(0 4px 12px rgba(102,126,234,.3))}
            h2{color:#e8e8ff;margin-bottom:4px;font-size:1.5em;font-weight:700;letter-spacing:-.5px}
            .sub{color:rgba(255,255,255,.4);font-size:.9em;margin-bottom:28px}
            .input-group{position:relative;margin-bottom:14px}
            .input-group .icon{position:absolute;left:16px;top:50%;transform:translateY(-50%);color:rgba(255,255,255,.25);font-size:16px;pointer-events:none}
            .input-group input{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none}
            .input-group input::placeholder{color:rgba(255,255,255,.2)}
            .input-group input:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.06);box-shadow:0 0 0 3px rgba(102,126,234,.1)}
            .btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}
            .btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}
            .btn:active{transform:translateY(0)}
            .err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:12px 16px;border-radius:10px;margin-bottom:18px;font-size:.85em;text-align:left}
            .footer{margin-top:24px;display:flex;flex-direction:column;gap:10px}
            .footer a{color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}
            .footer a:hover{color:#667eea}
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style></head><body>
            <div class="box">
            <span class="logo">&#128218;</span>
            <h2>Create Account</h2>
            <p class="sub">Join the eLibrary today</p>
            """ + (error != null ? "<div class='err'>" + error + "</div>" : "") + """
            <form method='POST'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' placeholder='Password' required></div>
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
            sendHtml(exchange, generateLoginPage("Invalid credentials.", false));
            return;
        }
        sendHtml(exchange, generateLoginPage(null, false));
    }

    private static String generateLoginPage(String error, boolean registered) {
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
            .input-group input{width:100%;padding:14px 16px 14px 46px;border:1px solid rgba(255,255,255,.08);border-radius:12px;font-size:14px;transition:all .3s;background:rgba(255,255,255,.04);color:#e0e0f0;outline:none}
            .input-group input::placeholder{color:rgba(255,255,255,.2)}
            .input-group input:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.06);box-shadow:0 0 0 3px rgba(102,126,234,.1)}
            .btn{width:100%;padding:14px;border:none;border-radius:12px;cursor:pointer;font-size:15px;font-weight:600;color:#fff;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);transition:all .3s;margin-top:8px;letter-spacing:.3px}
            .btn:hover{transform:translateY(-2px);box-shadow:0 8px 30px rgba(102,126,234,.35)}
            .btn:active{transform:translateY(0)}
            .err{color:#ff6b6b;background:rgba(255,107,107,.1);border:1px solid rgba(255,107,107,.2);padding:12px 16px;border-radius:10px;margin-bottom:18px;font-size:.85em;text-align:left}
            .footer{margin-top:24px;display:flex;flex-direction:column;gap:10px}
            .footer a{color:rgba(255,255,255,.35);font-size:.85em;text-decoration:none;transition:color .25s}
            .footer a:hover{color:#667eea}
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style></head><body>
            <div class="box">
            <span class="logo">&#128218;</span>
            <h2>Welcome Back</h2>
            <p class="sub">Sign in to your account</p>
            """);
        if (error != null) h.append("<div class='err'>").append(error).append("</div>");
        h.append("""
            <form method='POST'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' placeholder='Password' required></div>
            <button type='submit' class='btn'>Login</button>
            </form>
            <div class="footer">
            <a href='/signup'>No account? Sign up</a>
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

    // ── Dashboard (users) ──────────────────────────────────

    private static void handleDashboard(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        if (user == null) { redirect(exchange, "/login"); return; }
        if ("admin".equals(getRole(user))) { redirect(exchange, "/admin"); return; }

        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            String action = form.getOrDefault("action", "");
            String isbn = form.getOrDefault("isbn", "");

            if ("return".equals(action)) {
                borrows.removeIf(br -> br.isbn.equals(isbn) && br.username.equals(user));
            } else if ("cancel".equals(action)) {
                requests.removeIf(r -> r.getIsbn().equals(isbn) && r.getUsername().equals(user) && "pending".equals(r.getStatus()));
            } else {
                if (findBook(isbn) != null)
                    requests.add(new Request(isbn, user));
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
        long borrowed = borrows.size();

        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>Dashboard &mdash; eLibrary System</title><style>");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Inter','Segoe UI',sans-serif;min-height:100vh;padding:20px;position:relative}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=1600') center/cover no-repeat;filter:brightness(.08) blur(3px);pointer-events:none}");
        h.append(".container{max-width:1100px;margin:0 auto;position:relative;z-index:1}");
        h.append(".topbar{display:flex;justify-content:space-between;align-items:center;padding:16px 28px;background:rgba(16,16,36,.8);border:1px solid rgba(255,255,255,.06);border-radius:16px;margin-bottom:28px;backdrop-filter:blur(14px)}");
        h.append(".topbar .brand{color:#e0e0f0;font-size:1.05em;font-weight:600;letter-spacing:.3px}");
        h.append(".topbar .brand span{color:#667eea}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 20px;border:1px solid rgba(255,255,255,.08);border-radius:10px;font-size:.88em;transition:all .3s}");
        h.append(".topbar a:hover{color:#fff;border-color:#667eea;background:rgba(102,126,234,.12)}");
        h.append("h1{color:#e8e8ff;text-align:center;margin-bottom:4px;font-size:1.9em;font-weight:700;letter-spacing:-.5px}.sub{color:rgba(255,255,255,.35);text-align:center;margin-bottom:28px;font-size:.95em}");
        h.append(".stats{display:flex;gap:14px;margin-bottom:28px;flex-wrap:wrap}");
        h.append(".stat{padding:20px 24px;flex:1;min-width:130px;text-align:center;border-radius:14px;border:1px solid rgba(255,255,255,.06)}");
        h.append(".stat .n{font-size:2em;font-weight:700;display:block}.stat .l{font-size:.82em;margin-top:5px;opacity:.85;text-transform:uppercase;letter-spacing:.5px}");
        h.append(".stat.avail{background:rgba(39,174,96,.1);color:#2ecc71;border-color:rgba(39,174,96,.15)}");
        h.append(".stat.req{background:rgba(243,156,18,.08);color:#f1c40f;border-color:rgba(243,156,18,.12)}");
        h.append(".stat.bor{background:rgba(231,76,60,.08);color:#e74c3c;border-color:rgba(231,76,60,.12)}");
        h.append(".stat.tot{background:rgba(255,255,255,.03);color:rgba(255,255,255,.6);border-color:rgba(255,255,255,.06)}");
        h.append(".card{background:rgba(16,16,36,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:18px;padding:28px;margin-bottom:24px;box-shadow:0 8px 50px rgba(0,0,0,.3)}");
        h.append(".card h2{font-size:1.1em;color:#d0d0f0;margin-bottom:16px;display:flex;align-items:center;gap:10px;font-weight:600;letter-spacing:.2px}");
        h.append("table{width:100%;border-collapse:collapse}");
        h.append("th{padding:14px 12px;text-align:left;font-size:.75em;text-transform:uppercase;letter-spacing:.7px;color:rgba(255,255,255,.3);font-weight:700;border-bottom:1px solid rgba(255,255,255,.06)}");
        h.append("td{padding:14px 12px;border-bottom:1px solid rgba(255,255,255,.04);font-size:.9em;color:rgba(255,255,255,.65);transition:color .2s}");
        h.append("tr:hover td{color:rgba(255,255,255,.9);background:rgba(255,255,255,.03)}");
        h.append("tr:last-child td{border-bottom:none}");
        h.append(".sa{color:#2ecc71;font-weight:600;text-shadow:0 0 20px rgba(46,204,113,.15)}.sr{color:#f1c40f;font-weight:600}.sb{color:#e74c3c;font-weight:600}");
        h.append("select,input{padding:13px 18px;border:1px solid rgba(255,255,255,.06);border-radius:12px;font-size:14px;flex:1;min-width:0;transition:all .3s;background:rgba(255,255,255,.03);color:#e0e0f0;outline:none;appearance:none;-webkit-appearance:none;cursor:pointer}");
        h.append("select{background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='9' fill='%23666ea'%3E%3Cpath d='M1 1l6 6 6-6'/%3E%3C/svg%3E\");background-repeat:no-repeat;background-position:right 16px center;padding-right:44px}");
        h.append("select option{background:#151530;color:#e0e0f0;padding:12px}");
        h.append("select:hover,input:hover{border-color:rgba(102,126,234,.25);background:rgba(255,255,255,.05)}");
        h.append("select:focus,input:focus{border-color:rgba(102,126,234,.5);background:rgba(102,126,234,.07);box-shadow:0 0 0 4px rgba(102,126,234,.08)}");
        h.append(".btn{padding:13px 30px;border:none;border-radius:12px;cursor:pointer;font-size:13px;font-weight:600;color:#fff;transition:all .3s;white-space:nowrap;letter-spacing:.3px}");
        h.append(".btn-sm{padding:7px 16px;font-size:11px;border-radius:8px;letter-spacing:.2px}");
        h.append(".btn:hover{transform:translateY(-3px)}");
        h.append(".btn:active{transform:translateY(-1px)}");
        h.append(".btn-pri{background:linear-gradient(135deg,#667eea,#764ba2);box-shadow:0 4px 20px rgba(102,126,234,.2)}");
        h.append(".btn-pri:hover{box-shadow:0 8px 30px rgba(102,126,234,.35)}");
        h.append(".btn-suc{background:linear-gradient(135deg,#27ae60,#2ecc71);box-shadow:0 4px 20px rgba(39,174,96,.2)}");
        h.append(".btn-suc:hover{box-shadow:0 8px 30px rgba(39,174,96,.35)}");
        h.append(".btn-rj{background:linear-gradient(135deg,#e74c3c,#ff6b6b);box-shadow:0 4px 20px rgba(231,76,60,.15)}");
        h.append(".btn-rj:hover{box-shadow:0 8px 25px rgba(231,76,60,.3)}");
        h.append(".btn-rj{background:linear-gradient(135deg,#e74c3c,#ff6b6b);box-shadow:0 4px 20px rgba(231,76,60,.15)}");
        h.append(".btn-rj:hover{box-shadow:0 8px 25px rgba(231,76,60,.3)}");
        h.append(".empty{text-align:center;padding:35px;color:rgba(255,255,255,.2);font-size:.9em}");
        h.append(".filter-row{display:flex;gap:14px;flex-wrap:wrap;margin-bottom:18px}");
        h.append(".filter-row select{flex:1;min-width:0}");
h.append(".form-row{display:flex;gap:12px;flex-wrap:wrap;align-items:center}");
h.append(".form-label{color:#c8c8e8;font-size:.9em;font-weight:600;margin-bottom:8px;display:block}");
h.append(".form-group{flex:1;min-width:210px}");
        h.append("@media(max-width:600px){body{padding:12px}.stats{gap:10px}.stat{padding:15px}.stat .n{font-size:1.4em}}");
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
        h.append("function populateLevel(){var t=document.getElementById('ftype').value;if(!t){resetFilters(true);return}var lv=document.getElementById('flevel');lv.innerHTML='<option value=\"\">Select level...</option>';lv.disabled=false;var seen={};books.forEach(function(b){if(b.tp===t&&!seen[b.l]){seen[b.l]=true;lv.innerHTML+='<option value=\"'+b.l+'\">'+b.l+'</option>'}});lv.value='';document.getElementById('fclass').innerHTML='<option value=\"\">Select class first...</option>';document.getElementById('fclass').disabled=true;filterBooks()}");
        h.append("function populateClass(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;if(!lv){document.getElementById('fclass').innerHTML='<option value=\"\">Select class first...</option>';document.getElementById('fclass').disabled=true;filterBooks();return}var cls=document.getElementById('fclass');cls.innerHTML='<option value=\"\">Select class...</option>';cls.disabled=false;var seen={};books.forEach(function(b){if(b.tp===t&&b.l===lv&&!seen[b.c]){seen[b.c]=true;cls.innerHTML+='<option value=\"'+b.c+'\">'+b.c+'</option>'}});filterBooks()}");
        h.append("function filterBooks(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;var cl=document.getElementById('fclass').value;if(!t&&!lv&&!cl){document.getElementById('btable').innerHTML='<tr><td colspan=3 class=empty>Select a filter above to browse books</td></tr>';return}var html='';books.forEach(function(b){if((!t||b.tp===t)&&(!lv||b.l===lv)&&(!cl||b.c===cl)){html+='<tr><td>'+b.i+'</td><td>'+b.t+'</td><td>'+b.a+'</td></tr>'}});document.getElementById('btable').innerHTML=html||'<tr><td colspan=3 class=empty>No books found</td></tr>';populateRequest()}");
h.append("function populateRequest(){var t=document.getElementById('ftype').value;var lv=document.getElementById('flevel').value;var cl=document.getElementById('fclass').value;var sel=document.getElementById('reqisbn');sel.innerHTML='<option value=\"\">Request book...</option>';books.forEach(function(b){if((!t||b.tp===t)&&(!lv||b.l===lv)&&(!cl||b.c===cl)){sel.innerHTML+='<option value=\"'+b.i+'\">'+b.t+'</option>'}})}");
h.append("populateRequest()");
        
        h.append("</script></head><body><div class='container'>");

        h.append("<div class='topbar'><div class='brand'>&#128218; <span>e</span>Library</div><div><a href='/logout'>Logout</a></div></div>");
        h.append("<h1>My eLibrary</h1><p class='sub'>Welcome back, <strong>").append(username).append("</strong></p>");

        // Stats
        long pendingReq = requests.stream().filter(r -> "pending".equals(r.getStatus())).count();
        h.append("<div class='stats'>");
        h.append("<div class='stat avail'><span class='n'>").append(avail).append("</span><span class='l'>Available</span></div>");
        h.append("<div class='stat req'><span class='n'>").append(pendingReq).append("</span><span class='l'>Requests</span></div>");
        h.append("<div class='stat bor'><span class='n'>").append(borrowed).append("</span><span class='l'>Borrowed</span></div>");
        h.append("<div class='stat tot'><span class='n'>").append(library.size()).append("</span><span class='l'>Total Books</span></div>");
        h.append("</div>");

        // Browse Books
        h.append("<div class='card'><h2>&#128214; Browse Books</h2>");
        h.append("<div class='filter-row'><select id='ftype' onchange='populateLevel()'><option value=''>Select type...</option>");
        Set<String> types = new LinkedHashSet<>();
        for (Book b : library) types.add(b.getType());
        for (String t : types) h.append("<option value='").append(t).append("'>").append(t).append("</option>");
h.append("</select><select id='flevel' onchange='populateClass()' disabled><option value=''>Select level first...</option></select>");
h.append("<select id='fclass' onchange='filterBooks()' disabled><option value=''>Select class first...</option></select>");
h.append("<select name='isbn' id='reqisbn' form='reqform' required><option value=''>Request book...</option></select>");
h.append("<button type='submit' class='btn btn-pri' style='padding:10px 18px;font-size:12px' form='reqform'>Request</button></div>");
h.append("<form id='reqform' method='POST'></form>");
        h.append("<table><tr><th>ISBN</th><th>Title</th><th>Author</th></tr><tbody id='btable'>");
        h.append("<tr><td colspan='3' class='empty'>Select a filter above to browse books</td></tr>");
        h.append("</tbody></table></div>");

        // My Requests
        h.append("<div class='card'><h2>&#128203; My Requests</h2>");
        List<Request> myReqs = requests.stream().filter(r -> r.getUsername().equals(username) && "pending".equals(r.getStatus())).collect(Collectors.toList());
        if (!myReqs.isEmpty()) {
            h.append("<table><tr><th>Book</th><th>Author</th><th>Requested At</th><th>Action</th></tr>");
            for (Request r : myReqs) {
                Book b = findBook(r.getIsbn());
                String bookTitle = b != null ? b.getTitle() : r.getIsbn();
                String author = b != null ? b.getAuthor() : "";
                h.append("<tr><td>").append(bookTitle).append("</td><td>").append(author).append("</td><td class='sr'>").append(r.getTimestamp()).append("</td>")
                 .append("<td><form method='POST'><input type='hidden' name='action' value='cancel'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><button class='btn btn-rj btn-sm'>Cancel</button></form></td></tr>");
            }
            h.append("</table>");
        } else h.append("<p class='empty'>No requests yet.</p>");
        h.append("</div>");

        // My Borrowed Books
        h.append("<div class='card'><h2>&#128230; My Borrowed Books</h2>");
        List<BorrowRecord> myBorrows = borrows.stream().filter(br -> br.username.equals(username)).collect(Collectors.toList());
        if (!myBorrows.isEmpty()) {
            h.append("<table><tr><th>ISBN</th><th>Title</th><th>Author</th><th>Due Date</th><th>Status</th><th>Read</th></tr>");
            for (BorrowRecord br : myBorrows) {
                Book b = findBook(br.isbn);
                String title = b != null ? b.getTitle() : br.isbn;
                String author = b != null ? b.getAuthor() : "";
                long daysOver = br.getDaysOverdue();
                String sc = daysOver > 0 ? "sb" : "sa";
                String st = daysOver > 0 ? daysOver + " day(s) overdue" : "On time";
                long daysUntilDue = java.time.LocalDate.parse(br.dueDate).toEpochDay() - java.time.LocalDate.now().toEpochDay();
                String dueSc = daysUntilDue <= 5 ? "sb" : "";
                h.append("<tr><td>").append(br.isbn).append("</td><td>").append(title)
                 .append("</td><td>").append(author).append("</td>")
                 .append("<td class='").append(dueSc).append("'>").append(br.dueDate).append("</td>")
                 .append("<td class='").append(sc).append("'>").append(st).append("</td>")
                 .append("<td><a href='/read?isbn=").append(br.isbn).append("' class='btn btn-pri btn-sm' style='text-decoration:none'>Read</a></td></tr>");
            }
            h.append("</table>");
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
        h.append("<title>").append(book.getTitle()).append(" &mdash; eLibrary System</title><style>");
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Georgia','Merriweather',serif;min-height:100vh;position:relative}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=1600') center/cover no-repeat;filter:brightness(.08) blur(3px);pointer-events:none}");
        h.append(".topbar{position:relative;z-index:1;background:rgba(18,18,40,.9);backdrop-filter:blur(14px);border-bottom:1px solid rgba(255,255,255,.06);color:#e0e0f0;padding:14px 30px;display:flex;justify-content:space-between;align-items:center}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 18px;border:1px solid rgba(255,255,255,.08);border-radius:10px;font-size:.88em;transition:all .3s}");
        h.append(".topbar a:hover{color:#fff;border-color:#667eea}");
        h.append(".book-info{max-width:800px;margin:0 auto;padding:30px 20px 10px;position:relative;z-index:1}");
        h.append(".book-info h1{font-size:1.8em;color:#e8e8ff;font-weight:700;letter-spacing:-.5px}.book-info .author{color:rgba(255,255,255,.35);font-size:1.05em;margin-top:6px}");
        h.append(".due-bar{position:relative;z-index:1;background:rgba(18,18,40,.8);backdrop-filter:blur(14px);border:1px solid rgba(255,255,255,.06);border-radius:14px;padding:16px 24px;margin:20px auto;max-width:800px;display:flex;justify-content:space-between;align-items:center}");
        h.append(".due-bar .warn{color:#e74c3c;font-weight:600}.due-bar .ok{color:#2ecc71;font-weight:600}");
        h.append(".flipframe{position:relative;z-index:1;width:100%;height:calc(100vh - 160px);margin:0 auto;max-width:1000px;padding:0 10px 10px}");
        h.append(".flipframe iframe{width:100%;height:100%;border:none;border-radius:12px;box-shadow:0 8px 40px rgba(0,0,0,.3)}");
        h.append(".content{max-width:800px;margin:0 auto;padding:0 20px 40px;position:relative;z-index:1}");
        h.append(".content .card{background:rgba(18,18,40,.8);backdrop-filter:blur(14px);border:1px solid rgba(255,255,255,.06);border-radius:16px;padding:45px;box-shadow:0 8px 40px rgba(0,0,0,.25);line-height:1.9;font-size:1.05em;color:rgba(255,255,255,.8)}");
        h.append(".blocked{text-align:center;padding:100px 20px;position:relative;z-index:1}.blocked h2{color:#e74c3c;margin-bottom:16px;font-size:1.4em}.blocked p{color:rgba(255,255,255,.4);margin-bottom:28px}.blocked a{display:inline-block;padding:12px 32px;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border-radius:12px;text-decoration:none;font-weight:600;transition:all .3s}.blocked a:hover{transform:translateY(-2px);box-shadow:0 8px 25px rgba(102,126,234,.35)}");
        h.append("</style></head><body>");
        h.append("<div class='topbar'><span>").append(book.getTitle()).append("</span><a href='/dashboard'>&larr; Dashboard</a></div>");
        h.append("<div class='book-info'><h1>").append(book.getTitle()).append("</h1><p class='author'>by ").append(book.getAuthor()).append("</p></div>");

        if (br != null) {
            long daysUntilDue = java.time.LocalDate.parse(br.dueDate).toEpochDay() - java.time.LocalDate.now().toEpochDay();
            String warnClass = daysUntilDue <= 5 ? "warn" : "ok";
            String warnText = daysUntilDue <= 0 ? "OVERDUE" : (daysUntilDue <= 5 ? daysUntilDue + " day(s) remaining" : "Due: " + br.dueDate);
            h.append("<div class='due-bar'><span class='").append(warnClass).append("'>").append(warnText).append("</span><span>Borrowed until ").append(br.dueDate).append("</span></div>");
        }

        if (blocked) {
            h.append("<div class='blocked'><h2>").append(message != null ? message : "Access Denied").append("</h2><a href='/dashboard'>Back to Dashboard</a></div>");
        } else if (flipUrl != null) {
            h.append("<div class='flipframe'><iframe src='").append(flipUrl).append("' allowfullscreen></iframe></div>");
        } else {
            h.append("<div class='content'><div class='card'>").append(book.getContent()).append("</div></div>");
        }
        h.append("</body></html>");
        return h.toString();
    }

    private static void handleAdmin(HttpExchange exchange) throws Exception {
        String user = getSessionUser(exchange);
        boolean isAdmin = user != null && "admin".equals(getRole(user));

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
                                break;
                            }
                        }
                    }
                }
                case "reject" -> {
                    for (Request r : requests) {
                        if (r.getIsbn().equals(isbn) && r.getUsername().equals(reqUser) && "pending".equals(r.getStatus())) {
                            r.setStatus("rejected");
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
                    if (toRemove != null) users.remove(toRemove);
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
            @media(max-width:480px){.box{padding:35px 24px;border-radius:16px}}
            </style></head><body>
            <div class="box">
            <span class="logo">&#128274;</span>
            <h2>Librarian Login</h2>
            <p class='sub'>Staff access only</p>""" + (failed ? "<div class='err'>Invalid credentials</div>" : "") + """
            <form method='POST'>
            <input type='hidden' name='action' value='login'>
            <div class="input-group"><span class="icon">&#128100;</span><input type='text' name='username' placeholder='Librarian username' required></div>
            <div class="input-group"><span class="icon">&#128273;</span><input type='password' name='password' placeholder='Password' required></div>
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
        h.append("*{box-sizing:border-box;margin:0;padding:0}");
        h.append("body{font-family:'Inter','Segoe UI',sans-serif;min-height:100vh;padding:20px;position:relative}");
        h.append("body::before{content:'';position:fixed;top:0;left:0;width:100%;height:100%;background:url('https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=1600') center/cover no-repeat;filter:brightness(.08) blur(3px);pointer-events:none}");
        h.append(".container{max-width:1100px;margin:0 auto;position:relative;z-index:1}");
        h.append(".topbar{display:flex;justify-content:space-between;align-items:center;padding:16px 28px;background:rgba(16,16,36,.8);border:1px solid rgba(255,255,255,.06);border-radius:16px;margin-bottom:28px;backdrop-filter:blur(14px)}");
        h.append(".topbar .brand{color:#e0e0f0;font-size:1.05em;font-weight:600;letter-spacing:.3px}");
        h.append(".topbar .brand span{color:#e74c3c}");
        h.append(".topbar a{color:rgba(255,255,255,.4);text-decoration:none;padding:8px 20px;border:1px solid rgba(255,255,255,.08);border-radius:10px;font-size:.88em;transition:all .3s}");
        h.append(".topbar a:hover{color:#fff;border-color:#e74c3c;background:rgba(231,76,60,.12)}");
        h.append("h1{color:#e8e8ff;text-align:center;margin-bottom:4px;font-size:1.8em;font-weight:700;letter-spacing:-.5px}.sub{color:rgba(255,255,255,.35);text-align:center;margin-bottom:28px;font-size:.95em}");
        h.append(".card{background:rgba(16,16,36,.85);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.06);border-radius:18px;padding:28px;margin-bottom:24px;box-shadow:0 8px 50px rgba(0,0,0,.3)}");
        h.append(".card h2{font-size:1.1em;color:#d0d0f0;margin-bottom:16px;display:flex;align-items:center;gap:10px;font-weight:600;letter-spacing:.2px}");
        h.append("table{width:100%;border-collapse:collapse}");
        h.append("th{padding:14px 12px;text-align:left;font-size:.75em;text-transform:uppercase;letter-spacing:.7px;color:rgba(255,255,255,.3);font-weight:700;border-bottom:1px solid rgba(255,255,255,.06)}");
        h.append("td{padding:14px 12px;border-bottom:1px solid rgba(255,255,255,.04);font-size:.9em;color:rgba(255,255,255,.65);transition:color .2s}");
        h.append("tr:hover td{color:rgba(255,255,255,.9);background:rgba(255,255,255,.03)}");
        h.append("tr:last-child td{border-bottom:none}");
        h.append(".sa{color:#2ecc71;font-weight:600;text-shadow:0 0 20px rgba(46,204,113,.15)}.sb{color:#e74c3c;font-weight:600}");
        h.append(".pending{display:flex;justify-content:space-between;align-items:center;padding:18px 22px;background:rgba(255,255,255,.02);border:1px solid rgba(255,255,255,.06);border-radius:14px;margin-bottom:12px;flex-wrap:wrap;gap:14px;transition:all .3s}");
        h.append(".pending:hover{border-color:rgba(255,255,255,.1);background:rgba(255,255,255,.04)}");
        h.append(".pending .info{flex:1;color:rgba(255,255,255,.6);font-size:.92em}.pending .info strong{color:#e0e0f0}");
        h.append(".pending .btns{display:flex;gap:10px}");
        h.append("form{display:flex;gap:10px;flex-wrap:wrap;align-items:center}");
        h.append(".btn{padding:9px 20px;border:none;border-radius:10px;cursor:pointer;font-size:13px;font-weight:600;color:#fff;transition:all .3s;letter-spacing:.2px}");
        h.append(".btn:hover{transform:translateY(-2px)}");
        h.append(".btn:active{transform:translateY(0)}");
        h.append(".btn-ap{background:linear-gradient(135deg,#27ae60,#2ecc71);box-shadow:0 4px 15px rgba(39,174,96,.15)}");
        h.append(".btn-ap:hover{box-shadow:0 8px 25px rgba(39,174,96,.3)}");
        h.append(".btn-rj{background:linear-gradient(135deg,#e74c3c,#ff6b6b);box-shadow:0 4px 15px rgba(231,76,60,.15)}");
        h.append(".btn-rj:hover{box-shadow:0 8px 25px rgba(231,76,60,.3)}");
        h.append(".btn-dl{background:#e74c3c;padding:4px 12px;font-size:12px}");
        h.append(".empty{text-align:center;padding:30px;color:rgba(255,255,255,.25);font-size:.92em}");
        h.append("@media(max-width:600px){body{padding:12px}.pending{flex-direction:column}.pending .btns{width:100%}}");
        h.append("</style></head><body><div class='container'>");

        h.append("<div class='topbar'><div class='brand'>&#128274; <span>Librarian</span> Panel</div><div><a href='/logout'>Logout</a></div></div>");
        h.append("<h1>Librarian Panel</h1><p class='sub'>Manage book requests &amp; users</p>");

        // Pending requests
        h.append("<div class='card'><h2>&#128203; Pending Requests</h2>");
        List<Request> pending = requests.stream().filter(r -> "pending".equals(r.getStatus())).collect(Collectors.toList());
        if (!pending.isEmpty()) {
            for (Request r : pending) {
                Book b = findBook(r.getIsbn());
                String title = b != null ? b.getTitle() : r.getIsbn();
                h.append("<div class='pending'><div class='info'><strong>").append(title).append("</strong>")
                 .append(" &mdash; requested by <strong>").append(r.getUsername()).append("</strong>")
                 .append(" &mdash; ").append(r.getTimestamp())
                 .append("</div><div class='btns'>")
                 .append("<form method='POST'><input type='hidden' name='action' value='approve'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><input type='hidden' name='username' value='").append(r.getUsername()).append("'><button class='btn btn-ap'>Approve</button></form>")
                 .append("<form method='POST'><input type='hidden' name='action' value='reject'><input type='hidden' name='isbn' value='").append(r.getIsbn()).append("'><input type='hidden' name='username' value='").append(r.getUsername()).append("'><button class='btn btn-rj'>Reject</button></form>")
                 .append("</div></div>");
            }
        } else h.append("<p class='empty'>No pending requests.</p>");
        h.append("</div>");

        // Manage users
        h.append("<div class='card'><h2>&#128101; Manage Users</h2>");
        h.append("<table><tr><th>Username</th><th>Role</th><th>Action</th></tr>");
        for (User u : users) {
            h.append("<tr><td>").append(u.getUsername()).append("</td><td>").append(u.getRole()).append("</td><td>");
            if (!"admin".equals(u.getRole()))
                h.append("<form method='POST'><input type='hidden' name='action' value='deleteuser'><input type='hidden' name='username' value='").append(u.getUsername()).append("'><button class='btn btn-dl'>Delete</button></form>");
            else h.append("<span style='color:#aaa;font-size:.85em'>-</span>");
            h.append("</td></tr>");
        }
        h.append("</table></div>");

        // Borrowed books with fees
        h.append("<div class='card'><h2>&#128200; Borrowed Books &amp; Fees</h2>");
        if (!borrows.isEmpty()) {
            h.append("<table><tr><th>ISBN</th><th>Title</th><th>Borrower</th><th>Due Date</th><th>Status</th><th>Late Fee</th></tr>");
            for (BorrowRecord br : borrows) {
                Book b = findBook(br.isbn);
                String title = b != null ? b.getTitle() : br.isbn;
                long d = br.getDaysOverdue();
                double f = br.getLateFee();
                String sc = d > 0 ? "sb" : "sa";
                h.append("<tr><td>").append(br.isbn).append("</td><td>").append(title)
                 .append("</td><td>").append(br.username).append("</td>")
                 .append("<td>").append(br.dueDate).append("</td>")
                 .append("<td class='").append(sc).append("'>").append(d > 0 ? d + " day(s) overdue" : "On time").append("</td>")
                 .append("<td class='").append(sc).append("'>$").append(String.format("%.2f", f)).append("</td></tr>");
            }
            h.append("</table>");
        } else h.append("<p class='empty'>No borrowed books.</p>");
        h.append("</div>");

        // All books (read-only)
        h.append("<div class='card'><h2>&#128214; All Books</h2>");
        h.append("<table><tr><th>ISBN</th><th>Title</th><th>Author</th></tr>");
        for (Book b : library) {
            h.append("<tr><td>").append(b.getIsbn()).append("</td><td>").append(b.getTitle())
             .append("</td><td>").append(b.getAuthor()).append("</td></tr>");
        }
        h.append("</table></div></div></body></html>");
        return h.toString();
    }
}
