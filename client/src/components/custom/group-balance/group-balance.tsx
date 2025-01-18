import { Avatar } from '@/components/ui/avatar';
import { Balance, GroupMemberWithUser } from '@/types.ts';
import { Flex, Text } from '@chakra-ui/react';
import { FC } from 'react';

type MemberProps = {
  balance: Balance;
  member: GroupMemberWithUser;
};

export const GroupBalance: FC<MemberProps> = ({ balance, member }) => {
  const memberImage =
    member.userInfo?.image.key &&
    `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${member.userInfo.image.key}`;

  return (
    <Flex key={member.id} align="center" justify="space-between">
      <Flex align="center" gap={3}>
        <Avatar
          src={memberImage}
          name={`${member.nickname}`}
          variant="solid"
          color="textBg"
          bg="primary"
          contain="content"
          w="40px"
          h="40px"
          outlineWidth={2}
          outlineColor="primary"
          outlineOffset={-1}
          outlineStyle="solid"
          p={member.userInfo?.image.key ? 0 : 3}
        ></Avatar>
        <Flex direction="column">
          <Text
            color="textRaw"
            fontWeight="501"
            fontSize={{ base: 'sm', lg: 'md' }}
          >
            {member.nickname}
          </Text>
          <Text fontSize={{ base: 'xs', lg: 'sm' }} color="textDimmed">
            {balance.amount}$
          </Text>
        </Flex>
      </Flex>
    </Flex>
  );
};
