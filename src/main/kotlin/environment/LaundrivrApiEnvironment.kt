package environment

import io.github.cdimascio.dotenv.Dotenv

/**
 * This class is used to store environment variables that are used throughout the application.
 */
class LaundrivrApiEnvironment(dotenv: Dotenv) {

    val supabaseUrl = dotenv.get("SUPABASE_URL")
    val supabaseServiceRoleKey = dotenv.get("SUPABASE_SERVICE_ROLE_KEY")
    val supabaseDatabasePassword = dotenv.get("SUPABASE_DATABASE_PASSWORD")

    val squareAccessToken = dotenv.get("SQUARE_ACCESS_TOKEN")
    val squareLocationId = dotenv.get("SQUARE_LOCATION_ID")
    val squareEnvironment = dotenv.get("SQUARE_ENVIRONMENT")

    val port = dotenv.get("PORT").toInt()
}