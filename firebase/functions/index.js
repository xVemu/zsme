const logger = require('firebase-functions/logger')
const admin = require('firebase-admin')
const { onSchedule } = require('firebase-functions/v2/scheduler')

admin.initializeApp()

exports.notifyNews = onSchedule('0 16 * * *', async () => {
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

/** @returns Message */
function createMessage(post) {
  return {
    topic: 'news',
    android: {
      notification: {
        title: post.title.rendered,
        body: post.excerpt.rendered.replace(/(<([^>]+)>)/gi, '') /*strips HTML*/,
        eventTimestamp: new Date(post.date_gmt),
        imageUrl: post._embedded['wp:featuredmedia'][0].source_url,
      },
    },
  }
}
