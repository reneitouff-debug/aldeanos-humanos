package com.humanvillagers;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.Random;

/** Todo el texto y las reglas de "personalidad" viven aquí. */
public final class Dialogue {
    private static final Random RNG = new Random();

    public static final String[] PERSONALITY_NAMES = {"Amable", "Gruñón", "Tímido", "Alegre"};

    public static final String[] NAMES = {
            "Mateo", "Lucía", "Tomás", "Elena", "Bruno", "Carmen", "Hugo", "Rosa", "Andrés", "Marta",
            "Ignacio", "Paula", "Joaquín", "Inés", "Renato", "Valentina", "Gaspar", "Clara", "Fermín", "Olivia",
            "Isidro", "Camila", "Baltasar", "Noelia", "Ramiro", "Sofía", "Cipriano", "Aurora", "Lorenzo", "Beatriz"
    };

    /** Objetos que se pueden regalar y cuánto cariño dan. */
    public static final Map<Item, Integer> GIFTS = Map.ofEntries(
            Map.entry(Items.BREAD, 3), Map.entry(Items.APPLE, 3), Map.entry(Items.COOKIE, 3),
            Map.entry(Items.POPPY, 4), Map.entry(Items.DANDELION, 4), Map.entry(Items.BLUE_ORCHID, 5),
            Map.entry(Items.GOLDEN_CARROT, 6), Map.entry(Items.PUMPKIN_PIE, 7), Map.entry(Items.CAKE, 8),
            Map.entry(Items.EMERALD, 10), Map.entry(Items.DIAMOND, 15)
    );

    // [personalidad][nivel de amistad][variantes]
    // niveles: 0 desconocido, 1 conocido, 2 amigo, 3 mejor amigo
    private static final String[][][] LINES = {
            { // Amable
                    {"Hola, forastero. ¿Todo bien por aquí?", "Bienvenido a nuestra aldea."},
                    {"¡Ah, eres tú otra vez! Me alegra verte.", "¿Cómo te ha ido el viaje?"},
                    {"¡Qué gusto verte, amigo! Siempre eres bienvenido.", "Si necesitas algo, solo dilo."},
                    {"Eres como de la familia. Mi casa es tu casa.", "No sé qué haría esta aldea sin ti."}
            },
            { // Gruñón
                    {"¿Y tú qué quieres? Estoy ocupado.", "Hmpf. Otro viajero que viene a molestar."},
                    {"Ah, tú. Al menos no robas las zanahorias.", "No te acostumbres, pero... hola."},
                    {"Bah, supongo que no eres tan malo.", "Te toleraré un ratito más. No lo digas por ahí."},
                    {"...Está bien, me caes bien. ¡Pero no se lo cuentes a nadie!", "Eres el único que me aguanta. Gracias."}
            },
            { // Tímido
                    {"Eh... ho-hola... no te había visto...", "Perdón, ¿me hablabas a mí?"},
                    {"Oh, hola de nuevo... me alegra que vengas.", "Me pone nervioso hablar, pero tú pareces amable."},
                    {"¡Hola! Contigo ya no me da tanta vergüenza.", "Me gusta cuando vienes a visitarme."},
                    {"Eres mi mejor amigo. Nunca pensé decirlo en voz alta.", "Contigo puedo ser yo mismo. Gracias."}
            },
            { // Alegre
                    {"¡Hola, hola! ¡Un rostro nuevo! ¿Cómo te llamas?", "¡Qué día tan maravilloso para conocernos!"},
                    {"¡Mira quién volvió! ¡Qué alegría!", "¡Te estaba esperando! Bueno, casi."},
                    {"¡Mi amigo favorito! ¿Qué aventuras traes hoy?", "¡Contigo todo es más divertido!"},
                    {"¡Eres lo mejor que le pasó a esta aldea! ¡Te quiero un montón!", "¡Hagamos una fiesta! ¡Tú eres el invitado de honor!"}
            }
    };

    private static final String[] NIGHT = {"Ya es tarde... ¿no deberías estar durmiendo?", "Con tanto monstruo suelto, ten cuidado de noche."};
    private static final String[] RAIN = {"Qué lluvia... al menos los cultivos lo agradecen.", "Con esta lluvia solo quiero quedarme en casa."};
    private static final String[] MORNING = {"Buenos días. El sol recién sale y ya hay trabajo.", "Qué bonito amanecer, ¿verdad?"};

    private static final Map<String, String[]> PROFESSION = Map.ofEntries(
            Map.entry("farmer", new String[]{"La cosecha va bien este año.", "Las zanahorias no se plantan solas, ¿sabes?"}),
            Map.entry("librarian", new String[]{"Shh... estoy leyendo. Bueno, dime.", "Un buen libro vale más que una espada."}),
            Map.entry("armorer", new String[]{"Con una buena armadura sobrevivirás a un creeper.", "El hierro nunca miente."}),
            Map.entry("weaponsmith", new String[]{"Una espada afilada es una espada feliz.", "Ojalá nunca necesites lo que forjo."}),
            Map.entry("toolsmith", new String[]{"Cuida tus herramientas y ellas te cuidarán.", "Una pala rota es una tarde perdida."}),
            Map.entry("butcher", new String[]{"La carne fresca es lo mío.", "¿Hambre? Tengo de sobra."}),
            Map.entry("cleric", new String[]{"Las pociones requieren paciencia.", "Hay magia en cada frasco."}),
            Map.entry("fisherman", new String[]{"Hoy los peces no pican...", "La paciencia es la clave de la pesca."}),
            Map.entry("shepherd", new String[]{"Las ovejas son tercas, pero adorables.", "Lana de todos los colores, ¡pídeme lo que quieras!"}),
            Map.entry("fletcher", new String[]{"Una flecha bien hecha vuela recto.", "¿Sabes cuántas plumas hacen falta? Muchas."}),
            Map.entry("cartographer", new String[]{"El mundo es enorme. ¿Has ido lejos?", "Un mapa es una promesa de aventura."}),
            Map.entry("mason", new String[]{"Piedra sobre piedra, así se hace un hogar.", "El ladrillo nunca falla."}),
            Map.entry("leatherworker", new String[]{"El cuero bien curtido dura toda la vida.", "Ten, huele este cuero. Es de calidad."}),
            Map.entry("nitwit", new String[]{"¿Eh? ¿Qué? Ah, sí... hola... creo.", "Hoy no he hecho nada y estoy agotado."}),
            Map.entry("none", new String[]{"Todavía no sé qué quiero ser de mayor.", "Estoy buscando un oficio. ¿Alguna idea?"})
    );

    public static int tier(int friendship) {
        if (friendship < 10) return 0;
        if (friendship < 30) return 1;
        if (friendship < 60) return 2;
        return 3;
    }

    public static String tierName(int friendship) {
        if (friendship < 0) return "Enemistad";
        return new String[]{"Desconocido", "Conocido", "Amigo", "Mejor amigo"}[tier(friendship)];
    }

    public static String talkLine(int personality, int friendship, String professionPath, long timeOfDay, boolean raining) {
        if (friendship < 0) {
            return new String[]{"No tengo nada que decirte después de lo que hiciste.", "Aléjate de mí."}[RNG.nextInt(2)];
        }
        int roll = RNG.nextInt(100);
        if (roll < 20 && raining) return pick(RAIN);
        if (roll < 40 && timeOfDay > 13000 && timeOfDay < 23000) return pick(NIGHT);
        if (roll < 55 && timeOfDay < 3000) return pick(MORNING);
        if (roll < 75) return pick(PROFESSION.getOrDefault(professionPath, PROFESSION.get("none")));
        String[] base = LINES[Math.floorMod(personality, LINES.length)][tier(friendship)];
        return pick(base);
    }

    public static String giftThanks(int personality) {
        return switch (Math.floorMod(personality, 4)) {
            case 0 -> "¡Qué detalle tan amable! Muchas gracias.";
            case 1 -> "Hmpf... no tenías que hacerlo. Gracias.";
            case 2 -> "¿P-para mí? ¡Gracias, de verdad!";
            default -> "¡¡Un regalo!! ¡Eres el mejor!";
        };
    }

    public static String hurtLine(int personality) {
        return switch (Math.floorMod(personality, 4)) {
            case 0 -> "¡Ay! ¿Por qué hiciste eso?";
            case 1 -> "¡Te voy a... ay! ¡Aléjate!";
            case 2 -> "¡No me hagas daño, por favor!";
            default -> "¡Eso no fue nada gracioso!";
        };
    }

    public static String randomName() { return pick(NAMES); }
    public static int randomPersonality() { return RNG.nextInt(PERSONALITY_NAMES.length); }
    private static String pick(String[] arr) { return arr[RNG.nextInt(arr.length)]; }
}
