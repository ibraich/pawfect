package com.example.pawfect

import android.content.Context
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

class OpenAI(val context: Context) {

    val api_key = "sk-proj-R5RgCiV7PFW3HK46YXe5zNUQtxlw9XRd30d-62-LoWfKol716qbi2LuVSrXEVbMg7qTOZ4USZ" +
            "qT3BlbkFJrTFSPf7pkgl062JvcM9_KsvaWpp2tZr6DlrDLkUB1YSSO5JJhqnILIWFgAcKZzlAe2-zPbO2cA";

    val openai = OpenAI(
        token = api_key,
        timeout = Timeout(socket = 60.seconds),
    )

    suspend fun generateMatchOffspringImage(currentUser: UserFetch, friendUser: UserFetch): String? {

        val prompt = """
    Generate a **cute, artistic, and visually appealing** image of one or two **puppies**, representing the hypothetical offspring of the following two parent dogs:
    
    ### **Parent Dogs:**  
    - **Dog 1**  
      - Name: ${currentUser.dogName}  
      - Age: ${currentUser.dogAge}  
      - Breed: ${currentUser.dogBreed}  
      - Personality: ${currentUser.dogPersonality}  
      
    - **Dog 2**  
      - Name: ${friendUser.dogName}  
      - Age: ${friendUser.dogAge}  
      - Breed: ${friendUser.dogBreed}  
      - Personality: ${friendUser.dogPersonality}  
    
    ### **Puppy Appearance & Style:**  
    - The puppy (or puppies) should be a **blend of both parent dogs’ breeds and physical features**, reflecting an adorable combination.  
    - Use a **art style** with bright, playful colors.  
    - Their **fur patterns, ear shapes, and facial features should be a creative mix** of both parents.  
    - Expressions should be **playful, happy, and full of energy** to convey their personality.  
    
    Generate a **fun and visually delightful cartoon-style image** that highlights the unique blend of both parent dogs.
""".trimIndent()


        val images: List<ImageURL> = openai.imageURL(
            creation = ImageCreation(
                prompt = prompt,
                model = ModelId("dall-e-3"),
                n = 1,
                size = ImageSize.is1024x1024
            )
        )

        return images.firstOrNull()?.url
    }
}