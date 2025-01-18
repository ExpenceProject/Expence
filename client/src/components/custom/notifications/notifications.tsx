import { MenuContent, MenuRoot, MenuTrigger } from '@/components/ui/menu';
import {
  Invitation,
  InvitationStatus,
  InvitationWithInviter,
  InvitationWithInviterAndGroup,
} from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { Flex, Text } from '@chakra-ui/react';
import { DateTime } from 'luxon';
import { useEffect, useRef, useState } from 'react';
import { FaRegBell } from 'react-icons/fa';
import { toast } from 'react-toastify';

import { Invitation as InvitationComponent } from './invitation';

export const Notifications = () => {
  const { user } = useUser();
  const bellRef = useRef<HTMLDivElement>(null);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [invitations, setInvitations] = useState<
    InvitationWithInviterAndGroup[]
  >([]);

  const getAnchorRect = () => {
    const bell = bellRef.current;

    if (!bell) {
      return null;
    }

    return bell.getBoundingClientRect();
  };

  const acceptInvitation = async (invitationId: string) => {
    apiClient
      .patch(`/invitations/${invitationId}`, InvitationStatus.ACCEPTED, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then((response) => {
        const acceptedInvitation = invitations.find(
          (invitation) => invitation.id === invitationId,
        );
        setInvitations((invitations) =>
          invitations.filter((invitation) => invitation.id !== invitationId),
        );
        toast.success(
          `Successfully joined group ${acceptedInvitation?.group?.name}`,
        );
        return response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const declineInvitation = async (invitationId: string) => {
    apiClient
      .patch(`/invitations/${invitationId}`, InvitationStatus.DECLINED, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then((response) => {
        const declinedInvitation = invitations.find(
          (invitation) => invitation.id === invitationId,
        );
        setInvitations((invitations) =>
          invitations.filter((invitation) => invitation.id !== invitationId),
        );
        toast.info(
          `Declined invitation to group ${declinedInvitation?.group?.name}`,
        );
        return response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  };

  useEffect(() => {
    const getUserInvitations = async () => {
      return apiClient
        .get(`/invitations/invitees/${user?.id}`)
        .then(async (response) => {
          const invitations = response.data;
          if (!invitations.length) {
            return [];
          }

          const invitationsWithInviters = await Promise.all(
            invitations.map(async (invitation: Invitation) => {
              const inviter = await apiClient
                .get(
                  `/groups/${invitation.groupId}/members/${invitation.inviterId}/user`,
                )
                .then((response) => response.data)
                .catch((error) => {
                  console.error(error);
                  return null;
                });
              return {
                ...invitation,
                inviter,
              };
            }),
          );

          const invitationsWithInvitersAndGroups = await Promise.all(
            invitationsWithInviters.map(
              async (invitation: InvitationWithInviter) => {
                const group = await apiClient
                  .get(`/groups/${invitation.groupId}`)
                  .then((response) => response.data)
                  .catch((error) => {
                    console.error(error);
                    return null;
                  });
                return {
                  ...invitation,
                  group,
                };
              },
            ),
          );

          return invitationsWithInvitersAndGroups.filter(
            (invitation: InvitationWithInviterAndGroup) =>
              invitation.status === 'SENT',
          );
        })
        .catch((error) => {
          console.error(error);
          return [];
        });
    };

    getUserInvitations()
      .then((invitations) => {
        setInvitations(invitations);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [user]);

  if (!user) {
    return null;
  }

  return (
    <MenuRoot
      positioning={{
        offset: { crossAxis: 0, mainAxis: 21 },
        getAnchorRect,
        placement: 'bottom-end',
      }}
      open={isNotificationsOpen}
      onOpenChange={(e) => setIsNotificationsOpen(e.open)}
      onInteractOutside={() => setIsNotificationsOpen(false)}
      closeOnSelect={false}
    >
      <MenuTrigger asChild>
        <FaRegBell style={{ width: '20px', height: '20px' }} cursor="pointer" />
      </MenuTrigger>
      <MenuContent p={2.5} minW={0}>
        <Flex direction="column" gap={2}>
          {invitations.length > 0 ? (
            invitations.map((invitation) => {
              const inviterImage =
                invitation.inviter?.image.key &&
                `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${invitation.inviter?.image.key}`;

              const invitationCreationDate = DateTime.fromISO(
                invitation.createdAt,
              ).toRelative({ locale: 'en' });

              if (isNotificationsOpen)
                return (
                  <InvitationComponent
                    key={invitation.id}
                    invitation={invitation}
                    inviterImage={inviterImage}
                    acceptInvitation={acceptInvitation}
                    declineInvitation={declineInvitation}
                    invitationCreationDate={invitationCreationDate}
                  />
                );
            })
          ) : (
            <Text color="text" p={3}>
              You have no new notifications...
            </Text>
          )}
          {}
        </Flex>
      </MenuContent>
    </MenuRoot>
  );
};
