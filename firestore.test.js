const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user videos", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("videos").get());
});

test("Cross-user isolation: Alice cannot read Bob's videos", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("videos").doc("vid_bob").set({
      userId: BOB_UID,
      videoUrl: "https://example.com/v.mp4",
      thumbnailUrl: "https://example.com/t.jpg",
      creatorUsername: "bob_creator",
      caption: "Bob video",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("videos").doc("vid_bob").get());
});

test("Alice can create and read her own video", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const videoDoc = aliceDb.collection("users").doc(ALICE_UID).collection("videos").doc("vid_alice");
  
  await assertSucceeds(videoDoc.set({
    userId: ALICE_UID,
    videoUrl: "https://example.com/alice.mp4",
    thumbnailUrl: "https://example.com/alice.jpg",
    creatorUsername: "alice_creator",
    creatorName: "Alice Wonderland",
    caption: "My cool video!",
    tags: ["fun", "viral"],
    soundTitle: "Awesome Beat",
    soundAuthor: "DJ Alice",
    likesCount: 0,
    commentsCount: 0,
    bookmarksCount: 0,
    sharesCount: 0,
    createdAt: new Date(),
  }));

  await assertSucceeds(videoDoc.get());
});

test("Alice cannot create video with ghost field (Shadow Update test)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const videoDoc = aliceDb.collection("users").doc(ALICE_UID).collection("videos").doc("vid_bad");
  
  await assertFails(videoDoc.set({
    userId: ALICE_UID,
    videoUrl: "https://example.com/alice.mp4",
    thumbnailUrl: "https://example.com/alice.jpg",
    creatorUsername: "alice_creator",
    caption: "Ghost attack",
    isVerifiedAdmin: true, // Ghost field
    createdAt: new Date(),
  }));
});

test("Alice can save and update interactions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const interDoc = aliceDb.collection("users").doc(ALICE_UID).collection("interactions").doc("vid_1");

  await assertSucceeds(interDoc.set({
    userId: ALICE_UID,
    videoId: "vid_1",
    isLiked: true,
    isBookmarked: false,
    isShared: false,
    createdAt: new Date(),
  }));

  await assertSucceeds(interDoc.update({
    isBookmarked: true,
    updatedAt: new Date(),
  }));
});
