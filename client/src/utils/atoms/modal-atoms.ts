import { atom } from 'jotai';

// Login modal
export const isLoginModalOpenAtom = atom(false);

export const openLoginModalAtom = atom(null, (_, set) => {
  set(isLoginModalOpenAtom, true);
});

export const closeLoginModalAtom = atom(null, (_, set) => {
  set(isLoginModalOpenAtom, false);
});

// Register modal
export const isRegisterModalOpenAtom = atom(false);

export const openRegisterModalAtom = atom(null, (_, set) => {
  set(isRegisterModalOpenAtom, true);
});
export const closeRegisterModalAtom = atom(null, (_, set) => {
  set(isRegisterModalOpenAtom, false);
});

// Avatar modal

export const isAvatarModalOpenAtom = atom(false);

export const openAvatarModalAtom = atom(null, (_, set) => {
  set(isAvatarModalOpenAtom, true);
});

export const closeAvatarModalAtom = atom(null, (_, set) => {
  set(isAvatarModalOpenAtom, false);
});

// Group creation modal

export const isGroupCreationModalOpenAtom = atom(false);

export const openGroupCreationModalAtom = atom(null, (_, set) => {
  set(isGroupCreationModalOpenAtom, true);
});

export const closeGroupCreationModalAtom = atom(null, (_, set) => {
  set(isGroupCreationModalOpenAtom, false);
});

// Group edit image modal

export const isGroupEditImageModalOpenAtom = atom(false);

export const openGroupEditImageModalAtom = atom(null, (_, set) => {
  set(isGroupEditImageModalOpenAtom, true);
});

export const closeGroupEditImageModalAtom = atom(null, (_, set) => {
  set(isGroupEditImageModalOpenAtom, false);
});

// Membership invitation

export const isMembershipInvitationModalOpenAtom = atom(false);

export const openMembershipInvitationModalAtom = atom(null, (_, set) => {
  set(isMembershipInvitationModalOpenAtom, true);
});

export const closeMembershipInvitationModalAtom = atom(null, (_, set) => {
  set(isMembershipInvitationModalOpenAtom, false);

// Payment creation modal

export const isPaymentCreationModalOpenAtom = atom(false);

export const openPaymentCreationModalAtom = atom(null, (_, set) => {
  set(isPaymentCreationModalOpenAtom, true);
});

export const closePaymentCreationModalAtom = atom(null, (_, set) => {
  set(isPaymentCreationModalOpenAtom, false);
});
