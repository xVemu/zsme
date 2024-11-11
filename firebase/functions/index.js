import logger from 'firebase-functions/logger'
import admin from 'firebase-admin'
import { onSchedule } from 'firebase-functions/v2/scheduler'
import { convert } from 'html-to-text'

admin.initializeApp()

export const notifyNews = onSchedule('0 16 * * *', async () => {
// exports.notifyNews = onRequest(async () => { // For testing purposes.
  const config = await admin.remoteConfig()
  const template = await config.getTemplate()
  const { parameters: { apiUrl, newsId } } = template

  /** @type object[] */
  const posts = await fetch(
    `${apiUrl.defaultValue.value}/posts?per_page=78&_embed&fields=id,date,title,excerpt.rendered`)
    .then(v => v.json())

  const latestNews = newsId.defaultValue.value
  /** @type Message[] */
  const messages = []

  for (const post of posts) {
    // noinspection EqualityComparisonWithCoercionJS
    if (post.id == latestNews) break

    messages.push(createMessage(post))
  }

  if (messages.length <= 0) return

  const result = await admin.messaging().sendEach(messages)
  logger.log(`Notifications sent: ${result.successCount}`)

  newsId.defaultValue.value = `${posts[0].id}`
  await config.publishTemplate(template)
})

/** @type {Options}*/
const options = {
  wordwrap: false,
}

/** @returns Message */
function createMessage(post) {
  return {
    topic: 'news',
    android: {
      notification: {
        // Can't use Kotlin, because I couldn't find similar package
        title: convert(post.title.rendered, options),
        body: convert(post.excerpt.rendered, options),
        eventTimestamp: new Date(post.date_gmt),
        imageUrl: post._embedded['wp:featuredmedia'][0].source_url,
      },
    },
  }
}
